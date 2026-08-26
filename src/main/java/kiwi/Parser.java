package kiwi;

import java.time.LocalDate;
import kiwi.command.AddCommand;
import kiwi.command.Command;
import kiwi.command.DeleteCommand;
import kiwi.command.ExitCommand;
import kiwi.command.FindCommand;
import kiwi.command.ListCommand;
import kiwi.command.MarkCommand;
import kiwi.command.OnCommand;
import kiwi.command.UnmarkCommand;
import kiwi.task.Deadline;
import kiwi.task.Event;
import kiwi.task.KiwiDate;
import kiwi.task.Todo;

/**
 * Makes sense of a raw user command line and returns a {@link Command} ready to run.
 * Does not mutate the task list or print messages.
 */
public class Parser {

    /**
     * Parses one full input line into a {@link Command}.
     *
     * @param input full line typed by the user
     * @return a command object that can be executed
     * @throws KiwiException if the command is unknown or its arguments are invalid
     */
    public static Command parse(String input) throws KiwiException {
        if (input.equals("bye")) {
            return new ExitCommand();
        } else if (input.equals("list")) {
            return new ListCommand();
        } else if (input.equals("todo") || input.startsWith("todo ")) {
            String description = input.equals("todo") ? "" : input.substring("todo ".length()).trim();
            return new AddCommand(parseTodo(description));
        } else if (input.equals("deadline") || input.startsWith("deadline ")) {
            String body = input.equals("deadline") ? "" : input.substring("deadline ".length()).trim();
            return new AddCommand(parseDeadline(body));
        } else if (input.equals("event") || input.startsWith("event ")) {
            String body = input.equals("event") ? "" : input.substring("event ".length()).trim();
            return new AddCommand(parseEvent(body));
        } else if (input.equals("on") || input.startsWith("on ")) {
            String dateText = input.equals("on") ? "" : input.substring("on ".length()).trim();
            return new OnCommand(parseOnDate(dateText));
        } else if (input.equals("find") || input.startsWith("find ")) {
            String keyword = input.equals("find") ? "" : input.substring("find ".length()).trim();
            return new FindCommand(parseFindKeyword(keyword));
        } else if (input.equals("mark") || input.startsWith("mark ")) {
            return new MarkCommand(parseTaskNumber(input, "mark"));
        } else if (input.equals("unmark") || input.startsWith("unmark ")) {
            return new UnmarkCommand(parseTaskNumber(input, "unmark"));
        } else if (input.equals("delete") || input.startsWith("delete ")) {
            return new DeleteCommand(parseTaskNumber(input, "delete"));
        } else {
            throw new KiwiException(
                    "Hmm, Kiwi doesn't recognize that. Try todo, deadline, event, on, find, list, "
                            + "mark, unmark, delete, or bye.");
        }
    }

    /**
     * Builds a to-do from the description after {@code todo}.
     *
     * @param description task description
     * @return a new {@link Todo}
     * @throws KiwiException if the description is empty
     */
    private static Todo parseTodo(String description) throws KiwiException {
        if (description.isEmpty()) {
            throw new KiwiException("A todo needs a description — try: todo borrow book");
        }
        return new Todo(description);
    }

    /**
     * Parses {@code description /by yyyy-MM-dd} into a deadline task.
     *
     * @param body text after the {@code deadline} command
     * @return a new {@link Deadline}
     * @throws KiwiException if the description or {@code /by} date is missing/invalid
     */
    private static Deadline parseDeadline(String body) throws KiwiException {
        if (body.isEmpty()) {
            throw new KiwiException(
                    "A deadline needs details — try: deadline return book /by 2019-12-02");
        }
        String[] parts = body.split(" /by ", 2);
        if (parts.length < 2 || parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) {
            throw new KiwiException(
                    "Deadlines need both a description and /by yyyy-MM-dd — "
                            + "e.g. deadline return book /by 2019-12-02");
        }
        return new Deadline(parts[0].trim(), KiwiDate.parse(parts[1].trim()));
    }

    /**
     * Parses {@code description /from yyyy-MM-dd /to yyyy-MM-dd} into an event.
     *
     * @param body text after the {@code event} command
     * @return a new {@link Event}
     * @throws KiwiException if description or dates are missing/invalid
     */
    private static Event parseEvent(String body) throws KiwiException {
        if (body.isEmpty()) {
            throw new KiwiException(
                    "An event needs details — try: event meeting /from 2019-10-04 /to 2019-10-11");
        }
        String[] fromSplit = body.split(" /from ", 2);
        if (fromSplit.length < 2 || fromSplit[0].trim().isEmpty()) {
            throw new KiwiException(
                    "Events need /from and /to as yyyy-MM-dd — "
                            + "e.g. event meeting /from 2019-10-04 /to 2019-10-11");
        }
        String[] toSplit = fromSplit[1].split(" /to ", 2);
        if (toSplit.length < 2 || toSplit[0].trim().isEmpty() || toSplit[1].trim().isEmpty()) {
            throw new KiwiException(
                    "Events need /from and /to as yyyy-MM-dd — "
                            + "e.g. event meeting /from 2019-10-04 /to 2019-10-11");
        }
        LocalDate from = KiwiDate.parse(toSplit[0].trim());
        LocalDate to = KiwiDate.parse(toSplit[1].trim());
        return new Event(fromSplit[0].trim(), from, to);
    }

    /**
     * Checks that a {@code find} keyword is not empty.
     *
     * @param keyword search text after {@code find}
     * @return the keyword unchanged
     * @throws KiwiException if the keyword is missing
     */
    private static String parseFindKeyword(String keyword) throws KiwiException {
        if (keyword.isEmpty()) {
            throw new KiwiException("Please give a keyword to search for, e.g. find book");
        }
        return keyword;
    }

    /**
     * Parses the date argument of an {@code on} command.
     *
     * @param dateText {@code yyyy-MM-dd} date
     * @return the parsed date
     * @throws KiwiException if the date is missing or invalid
     */
    private static LocalDate parseOnDate(String dateText) throws KiwiException {
        if (dateText.isEmpty()) {
            throw new KiwiException("Please give a date, e.g. on 2019-12-02");
        }
        return KiwiDate.parse(dateText);
    }

    /**
     * Reads the 1-based task number from a mark/unmark/delete command.
     * Does not check whether the index exists in the list — commands do that at execute time.
     *
     * @param input   full command line
     * @param command {@code mark}, {@code unmark}, or {@code delete}
     * @return 0-based index
     * @throws KiwiException if the number is missing or not an integer
     */
    private static int parseTaskNumber(String input, String command) throws KiwiException {
        String[] parts = input.trim().split("\\s+");
        if (parts.length < 2) {
            throw new KiwiException("Please give a task number, e.g. " + command + " 1");
        }
        try {
            return Integer.parseInt(parts[1]) - 1;
        } catch (NumberFormatException e) {
            throw new KiwiException("That task number doesn't look like a number: " + parts[1]);
        }
    }
}
