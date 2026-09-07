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
    private static final String COMMAND_BYE = "bye";
    private static final String COMMAND_LIST = "list";
    private static final String COMMAND_TODO = "todo";
    private static final String COMMAND_DEADLINE = "deadline";
    private static final String COMMAND_EVENT = "event";
    private static final String COMMAND_ON = "on";
    private static final String COMMAND_FIND = "find";
    private static final String COMMAND_MARK = "mark";
    private static final String COMMAND_UNMARK = "unmark";
    private static final String COMMAND_DELETE = "delete";

    private static final String DELIMITER_BY = " /by ";
    private static final String DELIMITER_FROM = " /from ";
    private static final String DELIMITER_TO = " /to ";

    private static final String EVENT_USAGE =
            "Events need /from and /to as yyyy-MM-dd — "
                    + "e.g. event meeting /from 2019-10-04 /to 2019-10-11";

    /**
     * Parses one full input line into a {@link Command}.
     *
     * @param input full line typed by the user.
     * @return a command object that can be executed.
     * @throws KiwiException If the command is unknown or its arguments are invalid.
     */
    public static Command parse(String input) throws KiwiException {
        assert input != null : "Ui and GUI always pass a command line, never null";
        if (isCommand(input, COMMAND_BYE)) {
            return new ExitCommand();
        }
        if (isCommand(input, COMMAND_LIST)) {
            return new ListCommand();
        }
        if (isCommand(input, COMMAND_TODO)) {
            return new AddCommand(parseTodo(argumentOf(input, COMMAND_TODO)));
        }
        if (isCommand(input, COMMAND_DEADLINE)) {
            return new AddCommand(parseDeadline(argumentOf(input, COMMAND_DEADLINE)));
        }
        if (isCommand(input, COMMAND_EVENT)) {
            return new AddCommand(parseEvent(argumentOf(input, COMMAND_EVENT)));
        }
        if (isCommand(input, COMMAND_ON)) {
            return new OnCommand(parseOnDate(argumentOf(input, COMMAND_ON)));
        }
        if (isCommand(input, COMMAND_FIND)) {
            return new FindCommand(parseFindKeyword(argumentOf(input, COMMAND_FIND)));
        }
        if (isCommand(input, COMMAND_MARK)) {
            return new MarkCommand(parseTaskNumber(input, COMMAND_MARK));
        }
        if (isCommand(input, COMMAND_UNMARK)) {
            return new UnmarkCommand(parseTaskNumber(input, COMMAND_UNMARK));
        }
        if (isCommand(input, COMMAND_DELETE)) {
            return new DeleteCommand(parseTaskNumber(input, COMMAND_DELETE));
        }
        throw new KiwiException(
                "Hmm, Kiwi doesn't recognize that. Try todo, deadline, event, on, find, list, "
                        + "mark, unmark, delete, or bye.");
    }

    /**
     * Returns whether {@code input} is exactly {@code commandWord}, or that word followed by arguments.
     */
    private static boolean isCommand(String input, String commandWord) {
        return input.equals(commandWord) || input.startsWith(commandWord + " ");
    }

    /**
     * Returns the trimmed text after {@code commandWord}, or an empty string if there is none.
     */
    private static String argumentOf(String input, String commandWord) {
        if (input.equals(commandWord)) {
            return "";
        }
        return input.substring(commandWord.length() + 1).trim();
    }

    /**
     * Builds a to-do from the description after {@code todo}.
     *
     * @param description task description.
     * @return a new {@link Todo}.
     * @throws KiwiException If the description is empty.
     */
    private static Todo parseTodo(String description) throws KiwiException {
        if (description.isEmpty()) {
            throw new KiwiException("A todo needs a description — try: todo borrow book");
        }
        assert !description.isEmpty() : "Todo description is non-empty after the user-input check";
        return new Todo(description);
    }

    /**
     * Parses {@code description /by yyyy-MM-dd} into a deadline task.
     *
     * @param body text after the {@code deadline} command.
     * @return a new {@link Deadline}.
     * @throws KiwiException If the description or {@code /by} date is missing or invalid.
     */
    private static Deadline parseDeadline(String body) throws KiwiException {
        if (body.isEmpty()) {
            throw new KiwiException(
                    "A deadline needs details — try: deadline return book /by 2019-12-02");
        }
        String[] parts = body.split(DELIMITER_BY, 2);
        boolean hasBySeparator = parts.length == 2;
        boolean hasDescription = hasBySeparator && !parts[0].trim().isEmpty();
        boolean hasByDate = hasBySeparator && !parts[1].trim().isEmpty();
        if (!hasDescription || !hasByDate) {
            throw new KiwiException(
                    "Deadlines need both a description and /by yyyy-MM-dd — "
                            + "e.g. deadline return book /by 2019-12-02");
        }
        assert parts.length == 2 : "Deadline body has description and /by after validation";
        String description = parts[0].trim();
        String byText = parts[1].trim();
        return new Deadline(description, KiwiDate.parse(byText));
    }

    /**
     * Parses {@code description /from yyyy-MM-dd /to yyyy-MM-dd} into an event.
     *
     * @param body text after the {@code event} command.
     * @return a new {@link Event}.
     * @throws KiwiException If the description or dates are missing or invalid.
     */
    private static Event parseEvent(String body) throws KiwiException {
        if (body.isEmpty()) {
            throw new KiwiException(
                    "An event needs details — try: event meeting /from 2019-10-04 /to 2019-10-11");
        }
        String[] fromSplit = body.split(DELIMITER_FROM, 2);
        boolean hasFromSeparator = fromSplit.length == 2;
        boolean hasDescription = hasFromSeparator && !fromSplit[0].trim().isEmpty();
        if (!hasDescription) {
            throw new KiwiException(EVENT_USAGE);
        }
        assert fromSplit.length == 2 : "Event body has description and /from after validation";
        String[] toSplit = fromSplit[1].split(DELIMITER_TO, 2);
        boolean hasToSeparator = toSplit.length == 2;
        boolean hasFromDate = hasToSeparator && !toSplit[0].trim().isEmpty();
        boolean hasToDate = hasToSeparator && !toSplit[1].trim().isEmpty();
        if (!hasFromDate || !hasToDate) {
            throw new KiwiException(EVENT_USAGE);
        }
        assert toSplit.length == 2 : "Event body has /from and /to after validation";
        String description = fromSplit[0].trim();
        LocalDate from = KiwiDate.parse(toSplit[0].trim());
        LocalDate to = KiwiDate.parse(toSplit[1].trim());
        return new Event(description, from, to);
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
        assert !keyword.isEmpty() : "Find keyword is non-empty after the user-input check";
        return keyword;
    }

    /**
     * Parses the date argument of an {@code on} command.
     *
     * @param dateText {@code yyyy-MM-dd} date.
     * @return the parsed date.
     * @throws KiwiException If the date is missing or invalid.
     */
    private static LocalDate parseOnDate(String dateText) throws KiwiException {
        if (dateText.isEmpty()) {
            throw new KiwiException("Please give a date, e.g. on 2019-12-02");
        }
        assert !dateText.isEmpty() : "On-command date text is present after the user-input check";
        return KiwiDate.parse(dateText);
    }

    /**
     * Reads the 1-based task number from a mark/unmark/delete command.
     * Does not check whether the index exists in the list — commands do that at execute time.
     *
     * @param input full command line.
     * @param command {@code mark}, {@code unmark}, or {@code delete}.
     * @return 0-based index.
     * @throws KiwiException If the number is missing or not an integer.
     */
    private static int parseTaskNumber(String input, String command) throws KiwiException {
        String[] parts = input.trim().split("\\s+");
        if (parts.length < 2) {
            throw new KiwiException("Please give a task number, e.g. " + command + " 1");
        }
        assert parts.length >= 2 : "Task-number token exists after the missing-argument check";
        try {
            int userNumber = Integer.parseInt(parts[1]);
            return userNumber - 1;
        } catch (NumberFormatException e) {
            throw new KiwiException("That task number doesn't look like a number: " + parts[1]);
        }
    }
}
