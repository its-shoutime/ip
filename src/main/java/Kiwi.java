import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Kiwi is a simple chatbot that stores to-dos, deadlines, and events in memory,
 * lists them, and can mark, unmark, or delete them.
 */
public class Kiwi {
    /** Dynamically sized list of tasks (grows/shrinks as the user adds or deletes). */
    private static final ArrayList<Task> tasks = new ArrayList<>();

    private static final Ui ui = new Ui();

    public static void main(String[] args) {
        tasks.addAll(Storage.load());

        ui.showWelcome();

        String input = ui.readCommand();
        while (!input.equals("bye")) {
            ui.showLine();
            try {
                handleCommand(input);
            } catch (KiwiException e) {
                ui.showError(e.getMessage());
            }
            ui.showLine();
            input = ui.readCommand();
        }

        ui.showGoodbye();
    }

    /**
     * Runs one user command, or throws {@link KiwiException} for bad input.
     *
     * @param input full line typed by the user
     * @throws KiwiException if the command is unknown or incomplete
     */
    private static void handleCommand(String input) throws KiwiException {
        if (input.equals("list")) {
            listTasks();
        } else if (input.equals("todo") || input.startsWith("todo ")) {
            addTodo(input.equals("todo") ? "" : input.substring("todo ".length()).trim());
        } else if (input.equals("deadline") || input.startsWith("deadline ")) {
            addDeadline(input.equals("deadline") ? "" : input.substring("deadline ".length()).trim());
        } else if (input.equals("event") || input.startsWith("event ")) {
            addEvent(input.equals("event") ? "" : input.substring("event ".length()).trim());
        } else if (input.equals("on") || input.startsWith("on ")) {
            listTasksOn(input.equals("on") ? "" : input.substring("on ".length()).trim());
        } else if (input.equals("mark") || input.startsWith("mark ")) {
            markDone(parseTaskNumber(input, "mark"));
        } else if (input.equals("unmark") || input.startsWith("unmark ")) {
            markUndone(parseTaskNumber(input, "unmark"));
        } else if (input.equals("delete") || input.startsWith("delete ")) {
            deleteTask(parseTaskNumber(input, "delete"));
        } else {
            throw new KiwiException(
                    "Hmm, Kiwi doesn't recognize that. Try todo, deadline, event, on, list, "
                            + "mark, unmark, delete, or bye.");
        }
    }

    /**
     * Adds a to-do if the description is present.
     *
     * @param description task description after the {@code todo} command
     * @throws KiwiException if the description is empty
     */
    private static void addTodo(String description) throws KiwiException {
        if (description.isEmpty()) {
            throw new KiwiException("A todo needs a description — try: todo borrow book");
        }
        addTask(new Todo(description));
    }

    /**
     * Parses {@code description /by yyyy-MM-dd} and adds a deadline task.
     *
     * @param body text after the {@code deadline} command
     * @throws KiwiException if the description or {@code /by} date is missing/invalid
     */
    private static void addDeadline(String body) throws KiwiException {
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
        addTask(new Deadline(parts[0].trim(), KiwiDate.parse(parts[1].trim())));
    }

    /**
     * Parses {@code description /from yyyy-MM-dd /to yyyy-MM-dd} and adds an event.
     *
     * @param body text after the {@code event} command
     * @throws KiwiException if description or dates are missing/invalid
     */
    private static void addEvent(String body) throws KiwiException {
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
        addTask(new Event(fromSplit[0].trim(), from, to));
    }

    /**
     * Stores a task and prints the standard "Got it" confirmation.
     *
     * @param task task to add
     */
    private static void addTask(Task task) {
        tasks.add(task);
        Storage.save(tasks);
        ui.showTaskAdded(task, tasks.size());
    }

    /** Prints all stored tasks with 1-based numbering. */
    private static void listTasks() {
        ui.showTaskList(tasks);
    }

    /**
     * Prints deadlines due on {@code dateText} and events whose range covers that day.
     *
     * @param dateText {@code yyyy-MM-dd} date
     * @throws KiwiException if the date is missing or invalid
     */
    private static void listTasksOn(String dateText) throws KiwiException {
        if (dateText.isEmpty()) {
            throw new KiwiException("Please give a date, e.g. on 2019-12-02");
        }
        LocalDate date = KiwiDate.parse(dateText);
        ui.showTasksOn(date, tasks);
    }

    /**
     * Reads the 1-based task number from a mark/unmark/delete command.
     *
     * @param input   full command line
     * @param command {@code mark}, {@code unmark}, or {@code delete}
     * @return 0-based index into {@link #tasks}
     * @throws KiwiException if the number is missing, not an integer, or out of range
     */
    private static int parseTaskNumber(String input, String command) throws KiwiException {
        String[] parts = input.trim().split("\\s+");
        if (parts.length < 2) {
            throw new KiwiException("Please give a task number, e.g. " + command + " 1");
        }
        int index;
        try {
            index = Integer.parseInt(parts[1]) - 1;
        } catch (NumberFormatException e) {
            throw new KiwiException("That task number doesn't look like a number: " + parts[1]);
        }
        if (index < 0 || index >= tasks.size()) {
            throw new KiwiException("There is no task number " + (index + 1) + " in your list.");
        }
        return index;
    }

    private static void markDone(int index) {
        Task task = tasks.get(index);
        task.markAsDone();
        Storage.save(tasks);
        ui.showMarked(index + 1, task);
    }

    private static void markUndone(int index) {
        Task task = tasks.get(index);
        task.markAsNotDone();
        Storage.save(tasks);
        ui.showUnmarked(index + 1, task);
    }

    /**
     * Removes the task at the given index ({@link ArrayList#remove(int)} shifts later items).
     *
     * @param index 0-based position of the task to remove
     */
    private static void deleteTask(int index) {
        Task removed = tasks.remove(index);
        Storage.save(tasks);
        ui.showTaskDeleted(removed, tasks.size());
    }
}
