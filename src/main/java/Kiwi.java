/**
 * Kiwi is a simple chatbot that stores to-dos, deadlines, and events in memory,
 * lists them, and can mark, unmark, or delete them.
 * <p>
 * This class wires {@link Ui}, {@link Parser}, {@link TaskList}, and {@link Storage}
 * together: it reads input, asks the parser what was meant, then updates the list
 * and talks to the user.
 */
public class Kiwi {
    /** Default save file used when launching from {@link #main(String[])}. */
    public static final String DEFAULT_FILE_PATH = "./data/kiwi.txt";

    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;

    /**
     * Creates a Kiwi chatbot that loads tasks from {@code filePath}.
     *
     * @param filePath path to the task save file
     */
    public Kiwi(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        tasks = new TaskList(storage.load());
    }

    /**
     * Runs the chatbot loop until the user types {@code bye}.
     */
    public void run() {
        ui.showWelcome();

        String input = ui.readCommand();
        while (!input.equals("bye")) {
            ui.showLine();
            try {
                execute(Parser.parse(input));
            } catch (KiwiException e) {
                ui.showError(e.getMessage());
            }
            ui.showLine();
            input = ui.readCommand();
        }

        ui.showGoodbye();
    }

    /**
     * Carries out one already-parsed command: update tasks, save, and show feedback.
     *
     * @param command structured command from {@link Parser#parse(String)}
     * @throws KiwiException if a task number is out of range
     */
    private void execute(Parser.ParsedCommand command) throws KiwiException {
        switch (command.getType()) {
        case LIST:
            ui.showTaskList(tasks);
            break;
        case TODO:
        case DEADLINE:
        case EVENT:
            addTask(command.getTask());
            break;
        case ON:
            ui.showTasksOn(command.getDate(), tasks);
            break;
        case MARK:
            markDone(requireValidIndex(command.getIndex()));
            break;
        case UNMARK:
            markUndone(requireValidIndex(command.getIndex()));
            break;
        case DELETE:
            deleteTask(requireValidIndex(command.getIndex()));
            break;
        default:
            throw new KiwiException("Unhandled command type: " + command.getType());
        }
    }

    /**
     * Ensures {@code index} refers to an existing task.
     *
     * @param index 0-based index from the parser
     * @return the same index if valid
     * @throws KiwiException if there is no task at that number
     */
    private int requireValidIndex(int index) throws KiwiException {
        if (!tasks.isValidIndex(index)) {
            throw new KiwiException("There is no task number " + (index + 1) + " in your list.");
        }
        return index;
    }

    /**
     * Stores a task, saves to disk, and prints the standard "Got it" confirmation.
     *
     * @param task task to add
     */
    private void addTask(Task task) {
        tasks.add(task);
        storage.save(tasks.getTasks());
        ui.showTaskAdded(task, tasks.size());
    }

    private void markDone(int index) {
        tasks.markDone(index);
        storage.save(tasks.getTasks());
        ui.showMarked(index + 1, tasks.get(index));
    }

    private void markUndone(int index) {
        tasks.markNotDone(index);
        storage.save(tasks.getTasks());
        ui.showUnmarked(index + 1, tasks.get(index));
    }

    /**
     * Removes the task at the given index, saves, and confirms to the user.
     *
     * @param index 0-based position of the task to remove
     */
    private void deleteTask(int index) {
        Task removed = tasks.delete(index);
        storage.save(tasks.getTasks());
        ui.showTaskDeleted(removed, tasks.size());
    }

    public static void main(String[] args) {
        new Kiwi(DEFAULT_FILE_PATH).run();
    }
}
