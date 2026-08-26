package kiwi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import kiwi.task.Deadline;
import kiwi.task.Event;
import kiwi.task.Task;
import kiwi.task.Todo;

/**
 * Unit tests for {@link Storage#save(ArrayList)} and {@link Storage#load()}.
 */
class StorageTest {

    @TempDir
    Path tempDir;

    private Path saveFile;
    private Storage storage;

    @BeforeEach
    void setUp() {
        saveFile = tempDir.resolve("kiwi.txt");
        storage = new Storage(saveFile.toString());
    }

    @Test
    void load_missingFile_returnsEmptyList() {
        assertTrue(storage.load().isEmpty());
    }

    @Test
    void saveAndLoad_allTaskTypes_roundTripPreservesState() throws KiwiException {
        ArrayList<Task> tasks = new ArrayList<>();

        Todo todo = new Todo("read book");
        todo.markAsDone();
        tasks.add(todo);

        tasks.add(new Deadline("return book", LocalDate.of(2019, 12, 2)));

        tasks.add(new Event("meeting",
                LocalDate.of(2019, 10, 4),
                LocalDate.of(2019, 10, 11)));

        storage.save(tasks);

        ArrayList<Task> loaded = new Storage(saveFile.toString()).load();
        assertEquals(3, loaded.size());

        Todo loadedTodo = (Todo) loaded.get(0);
        assertEquals("read book", loadedTodo.getDescription());
        assertTrue(loadedTodo.isDone());

        Deadline loadedDeadline = (Deadline) loaded.get(1);
        assertEquals("return book", loadedDeadline.getDescription());
        assertEquals(LocalDate.of(2019, 12, 2), loadedDeadline.getBy());
        assertFalse(loadedDeadline.isDone());

        assertInstanceOf(Event.class, loaded.get(2));
        assertEquals("meeting", loaded.get(2).getDescription());
    }

    @Test
    void load_blankLinesAndCorruptedLines_skipsBadLinesAndLoadsValidOnes() throws IOException {
        Files.writeString(saveFile, """
                T | 0 | valid todo

                not a valid line
                D | 0 | return book | 2019-12-02
                """);

        ArrayList<Task> loaded = storage.load();

        assertEquals(2, loaded.size());
        assertInstanceOf(Todo.class, loaded.get(0));
        assertInstanceOf(Deadline.class, loaded.get(1));
    }

    @Test
    void load_doneFlagRestoredForEachTaskType() throws IOException, KiwiException {
        Files.writeString(saveFile, """
                T | 1 | done todo
                D | 1 | done deadline | 2019-12-02
                E | 0 | open event | 2019-10-04 | 2019-10-11
                """);

        ArrayList<Task> loaded = storage.load();

        assertTrue(loaded.get(0).isDone());
        assertTrue(loaded.get(1).isDone());
        assertFalse(loaded.get(2).isDone());
    }

    @Test
    void save_emptyList_writesEmptyFile() {
        storage.save(new ArrayList<>());

        ArrayList<Task> loaded = new Storage(saveFile.toString()).load();
        assertTrue(loaded.isEmpty());
        assertTrue(Files.exists(saveFile));
    }
}
