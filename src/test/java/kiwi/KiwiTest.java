package kiwi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Unit tests for {@link Kiwi#getResponse(String)}.
 */
class KiwiTest {

    @TempDir
    Path tempDir;

    private Kiwi kiwi;

    @BeforeEach
    void setUp() {
        kiwi = new Kiwi(tempDir.resolve("kiwi.txt").toString());
    }

    @Test
    void getResponse_todo_returnsAddedConfirmation() {
        String response = kiwi.getResponse("todo read book");
        assertEquals(
                "Got it. I've added this task:\n"
                        + "  [T][ ] read book\n"
                        + "Now you have 1 task in the list.",
                response);
    }

    @Test
    void getResponse_unknownCommand_returnsErrorMessage() {
        String response = kiwi.getResponse("jump");
        assertEquals(
                "Hmm, Kiwi doesn't recognize that. Try todo, deadline, event, on, find, list, "
                        + "mark, unmark, delete, or bye.",
                response);
    }

    @Test
    void getResponse_listAfterAdd_includesTheTask() {
        kiwi.getResponse("todo read book");
        String response = kiwi.getResponse("list");
        assertTrue(response.contains("1.[T][ ] read book"));
        assertFalse(kiwi.isExit());
    }

    @Test
    void getResponse_bye_setsIsExitAndReturnsGoodbye() {
        String response = kiwi.getResponse("bye");
        assertEquals("Bye. Hope to see you again soon!", response);
        assertTrue(kiwi.isExit());
    }
}
