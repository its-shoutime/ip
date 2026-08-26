package kiwi.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Unit tests for {@link TaskList}.
 */
class TaskListTest {

    private TaskList taskList;

    @BeforeEach
    void setUp() {
        taskList = new TaskList();
        taskList.add(new Todo("first"));
        taskList.add(new Todo("second"));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1})
    void isValidIndex_inRangeIndex_returnsTrue(int index) {
        assertTrue(taskList.isValidIndex(index));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 2, 10})
    void isValidIndex_outOfRangeIndex_returnsFalse(int index) {
        assertFalse(taskList.isValidIndex(index));
    }

    @Test
    void isValidIndex_emptyList_onlyRejectsNonNegativeIndices() {
        TaskList empty = new TaskList();
        assertFalse(empty.isValidIndex(0));
        assertFalse(empty.isValidIndex(-1));
    }

    @Test
    void add_increasesSizeAndAppendsTask() {
        taskList.add(new Todo("third"));
        assertEquals(3, taskList.size());
        assertEquals("third", taskList.get(2).getDescription());
    }

    @Test
    void delete_removesTaskAndReturnsIt() {
        Task removed = taskList.delete(0);
        assertEquals("first", removed.getDescription());
        assertEquals(1, taskList.size());
        assertEquals("second", taskList.get(0).getDescription());
    }

    @Test
    void markDone_marksTaskAtIndex() {
        taskList.markDone(1);
        assertTrue(taskList.get(1).isDone());
        assertFalse(taskList.get(0).isDone());
    }

    @Test
    void markNotDone_unmarksTaskAtIndex() {
        taskList.markDone(0);
        taskList.markNotDone(0);
        assertFalse(taskList.get(0).isDone());
    }
}
