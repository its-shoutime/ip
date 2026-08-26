import java.time.LocalDate;

/**
 * Makes sense of a raw user command line: identifies the command and parses
 * its arguments into values the rest of the app can use.
 * Does not mutate the task list or print messages.
 */
public class Parser {

    /** The kinds of commands Kiwi understands (except {@code bye}, handled in the main loop). */
    public enum CommandType {
        LIST, TODO, DEADLINE, EVENT, ON, MARK, UNMARK, DELETE
    }

    /**
     * Result of parsing one command: a type plus the values that command needs.
     * Only the fields relevant to {@link #type} are set; others are unused.
     */
    public static class ParsedCommand {
        private final CommandType type;
        private final Task task;
        private final LocalDate date;
        private final int index;

        private ParsedCommand(CommandType type, Task task, LocalDate date, int index) {
            this.type = type;
            this.task = task;
            this.date = date;
            this.index = index;
        }

        static ParsedCommand list() {
            return new ParsedCommand(CommandType.LIST, null, null, -1);
        }

        static ParsedCommand add(CommandType type, Task task) {
            return new ParsedCommand(type, task, null, -1);
        }

        static ParsedCommand on(LocalDate date) {
            return new ParsedCommand(CommandType.ON, null, date, -1);
        }

        static ParsedCommand indexed(CommandType type, int index) {
            return new ParsedCommand(type, null, null, index);
        }

        public CommandType getType() {
            return type;
        }

        public Task getTask() {
            return task;
        }

        public LocalDate getDate() {
            return date;
        }

        public int getIndex() {
            return index;
        }
    }

    /**
     * Parses one full input line into a {@link ParsedCommand}.
     *
     * @param input full line typed by the user
     * @return structured command ready for execution
     * @throws KiwiException if the command is unknown or its arguments are invalid
     */
    public static ParsedCommand parse(String input) throws KiwiException {
        if (input.equals("list")) {
            return ParsedCommand.list();
        } else if (input.equals("todo") || input.startsWith("todo ")) {
            String description = input.equals("todo") ? "" : input.substring("todo ".length()).trim();
            return ParsedCommand.add(CommandType.TODO, parseTodo(description));
        } else if (input.equals("deadline") || input.startsWith("deadline ")) {
            String body = input.equals("deadline") ? "" : input.substring("deadline ".length()).trim();
            return ParsedCommand.add(CommandType.DEADLINE, parseDeadline(body));
        } else if (input.equals("event") || input.startsWith("event ")) {
            String body = input.equals("event") ? "" : input.substring("event ".length()).trim();
            return ParsedCommand.add(CommandType.EVENT, parseEvent(body));
        } else if (input.equals("on") || input.startsWith("on ")) {
            String dateText = input.equals("on") ? "" : input.substring("on ".length()).trim();
            return ParsedCommand.on(parseOnDate(dateText));
        } else if (input.equals("mark") || input.startsWith("mark ")) {
            return ParsedCommand.indexed(CommandType.MARK, parseTaskNumber(input, "mark"));
        } else if (input.equals("unmark") || input.startsWith("unmark ")) {
            return ParsedCommand.indexed(CommandType.UNMARK, parseTaskNumber(input, "unmark"));
        } else if (input.equals("delete") || input.startsWith("delete ")) {
            return ParsedCommand.indexed(CommandType.DELETE, parseTaskNumber(input, "delete"));
        } else {
            throw new KiwiException(
                    "Hmm, Kiwi doesn't recognize that. Try todo, deadline, event, on, list, "
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
     * Does not check whether the index exists in the list — callers do that.
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
