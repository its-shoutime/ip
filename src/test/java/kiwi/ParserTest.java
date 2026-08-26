package kiwi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.LocalDate;
import kiwi.command.AddCommand;
import kiwi.command.Command;
import kiwi.command.DeleteCommand;
import kiwi.command.ExitCommand;
import kiwi.command.FindCommand;
import kiwi.command.ListCommand;
import kiwi.command.MarkCommand;
import kiwi.command.UnmarkCommand;
import kiwi.task.Deadline;
import kiwi.task.Event;
import kiwi.task.TaskList;
import kiwi.task.Todo;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Unit tests for {@link Parser#parse(String)}.
 */
class ParserTest {

    @TempDir
    Path tempDir;

    @Test
    void parse_bye_returnsExitCommand() throws KiwiException {
        assertInstanceOf(ExitCommand.class, Parser.parse("bye"));
    }

    @Test
    void parse_list_returnsListCommand() throws KiwiException {
        assertInstanceOf(ListCommand.class, Parser.parse("list"));
    }

    @Nested
    class TodoCommand {

        @Test
        void parse_validTodo_returnsAddCommandAndAddsTask() throws KiwiException {
            Command command = Parser.parse("todo read book");
            assertInstanceOf(AddCommand.class, command);

            TaskList tasks = new TaskList();
            command.execute(tasks, new Ui(), new Storage(tempDir.resolve("kiwi.txt").toString()));

            assertEquals(1, tasks.size());
            assertInstanceOf(Todo.class, tasks.get(0));
            assertEquals("read book", tasks.get(0).getDescription());
        }

        @Test
        void parse_todoWithoutDescription_throwsKiwiException() {
            KiwiException exception = assertThrows(KiwiException.class, () -> Parser.parse("todo"));
            assertEquals("A todo needs a description — try: todo borrow book", exception.getMessage());
        }
    }

    @Nested
    class DeadlineCommand {

        @Test
        void parse_validDeadline_returnsAddCommandAndAddsTask() throws KiwiException {
            Command command = Parser.parse("deadline return book /by 2019-12-02");
            assertInstanceOf(AddCommand.class, command);

            TaskList tasks = new TaskList();
            command.execute(tasks, new Ui(), new Storage(tempDir.resolve("kiwi.txt").toString()));

            assertEquals(1, tasks.size());
            Deadline deadline = (Deadline) tasks.get(0);
            assertEquals("return book", deadline.getDescription());
            assertEquals(LocalDate.of(2019, 12, 2), deadline.getBy());
        }

        @Test
        void parse_deadlineWithoutBody_throwsKiwiException() {
            KiwiException exception = assertThrows(KiwiException.class, () -> Parser.parse("deadline"));
            assertEquals("A deadline needs details — try: deadline return book /by 2019-12-02",
                    exception.getMessage());
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "deadline return book",
                "deadline  /by 2019-12-02",
                "deadline return book /by "
        })
        void parse_incompleteDeadline_throwsKiwiException(String input) {
            KiwiException exception = assertThrows(KiwiException.class, () -> Parser.parse(input));
            assertEquals(
                    "Deadlines need both a description and /by yyyy-MM-dd — "
                            + "e.g. deadline return book /by 2019-12-02",
                    exception.getMessage());
        }
    }

    @Nested
    class EventCommand {

        @Test
        void parse_validEvent_returnsAddCommandAndAddsTask() throws KiwiException {
            Command command = Parser.parse("event meeting /from 2019-10-04 /to 2019-10-11");
            assertInstanceOf(AddCommand.class, command);

            TaskList tasks = new TaskList();
            command.execute(tasks, new Ui(), new Storage(tempDir.resolve("kiwi.txt").toString()));

            assertEquals(1, tasks.size());
            Event event = (Event) tasks.get(0);
            assertEquals("meeting", event.getDescription());
            assertTrue(event.occursOn(LocalDate.of(2019, 10, 7)));
        }

        @Test
        void parse_eventWithoutBody_throwsKiwiException() {
            KiwiException exception = assertThrows(KiwiException.class, () -> Parser.parse("event"));
            assertEquals("An event needs details — try: event meeting /from 2019-10-04 /to 2019-10-11",
                    exception.getMessage());
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "event meeting",
                "event  /from 2019-10-04 /to 2019-10-11",
                "event meeting /from 2019-10-04",
                "event meeting /from  /to 2019-10-11",
                "event meeting /from 2019-10-04 /to "
        })
        void parse_incompleteEvent_throwsKiwiException(String input) {
            KiwiException exception = assertThrows(KiwiException.class, () -> Parser.parse(input));
            assertEquals(
                    "Events need /from and /to as yyyy-MM-dd — "
                            + "e.g. event meeting /from 2019-10-04 /to 2019-10-11",
                    exception.getMessage());
        }
    }

    @Nested
    class OnCommandParsing {

        @Test
        void parse_validOnDate_returnsOnCommand() throws KiwiException {
            assertInstanceOf(kiwi.command.OnCommand.class, Parser.parse("on 2019-12-02"));
        }

        @Test
        void parse_onWithoutDate_throwsKiwiException() {
            KiwiException exception = assertThrows(KiwiException.class, () -> Parser.parse("on"));
            assertEquals("Please give a date, e.g. on 2019-12-02", exception.getMessage());
        }
    }

    @Nested
    class FindCommandParsing {

        @Test
        void parse_findWithKeyword_returnsFindCommand() throws KiwiException {
            assertInstanceOf(FindCommand.class, Parser.parse("find book"));
        }

        @Test
        void parse_findWithoutKeyword_throwsKiwiException() {
            KiwiException exception = assertThrows(KiwiException.class, () -> Parser.parse("find"));
            assertEquals("Please give a keyword to search for, e.g. find book", exception.getMessage());
        }

        @Test
        void parse_findWithOnlySpaces_throwsKiwiException() {
            KiwiException exception = assertThrows(KiwiException.class, () -> Parser.parse("find   "));
            assertEquals("Please give a keyword to search for, e.g. find book", exception.getMessage());
        }
    }

    @Nested
    class TaskNumberCommands {

        @Test
        void parse_markCommand_marksTaskAtOneBasedIndex() throws KiwiException {
            TaskList tasks = new TaskList();
            tasks.add(new Todo("read book"));

            Command command = Parser.parse("mark 1");
            assertInstanceOf(MarkCommand.class, command);
            command.execute(tasks, new Ui(), new Storage(tempDir.resolve("kiwi.txt").toString()));

            assertTrue(tasks.get(0).isDone());
        }

        @Test
        void parse_unmarkCommand_unmarksTaskAtOneBasedIndex() throws KiwiException {
            TaskList tasks = new TaskList();
            Todo todo = new Todo("read book");
            todo.markAsDone();
            tasks.add(todo);

            Command command = Parser.parse("unmark 1");
            assertInstanceOf(UnmarkCommand.class, command);
            command.execute(tasks, new Ui(), new Storage(tempDir.resolve("kiwi.txt").toString()));

            assertTrue(!tasks.get(0).isDone());
        }

        @Test
        void parse_deleteCommand_removesTaskAtOneBasedIndex() throws KiwiException {
            TaskList tasks = new TaskList();
            tasks.add(new Todo("first"));
            tasks.add(new Todo("second"));

            Command command = Parser.parse("delete 2");
            assertInstanceOf(DeleteCommand.class, command);
            command.execute(tasks, new Ui(), new Storage(tempDir.resolve("kiwi.txt").toString()));

            assertEquals(1, tasks.size());
            assertEquals("first", tasks.get(0).getDescription());
        }

        @ParameterizedTest
        @ValueSource(strings = {"mark", "unmark", "delete"})
        void parse_taskNumberCommandWithoutNumber_throwsKiwiException(String commandWord) {
            KiwiException exception = assertThrows(KiwiException.class, () -> Parser.parse(commandWord));
            assertEquals("Please give a task number, e.g. " + commandWord + " 1", exception.getMessage());
        }

        @Test
        void parse_markWithNonNumericArgument_throwsKiwiException() {
            KiwiException exception = assertThrows(KiwiException.class, () -> Parser.parse("mark abc"));
            assertEquals("That task number doesn't look like a number: abc", exception.getMessage());
        }
    }

    @Test
    void parse_unknownCommand_throwsKiwiException() {
        KiwiException exception = assertThrows(KiwiException.class, () -> Parser.parse("jump"));
        assertEquals(
                "Hmm, Kiwi doesn't recognize that. Try todo, deadline, event, on, find, list, "
                        + "mark, unmark, delete, or bye.",
                exception.getMessage());
    }
}
