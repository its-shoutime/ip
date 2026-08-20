import java.util.Scanner;

/**
 * Kiwi is a simple chatbot that stores to-dos, deadlines, and events in memory,
 * lists them, and can mark or unmark them as done.
 */
public class Kiwi {
    private static final String LINE = "____________________________________________________________";
    private static final int MAX_TASKS = 100;

    private static final String BANNER = " _  ___          _ \n"
            + "| |/ (_)_      _(_)\n"
            + "| ' /| \\ \\ /\\ / / |\n"
            + "| . \\| |\\ V  V /| |\n"
            + "|_|\\_\\_| \\_/\\_/ |_|\n";

    private static final Task[] tasks = new Task[MAX_TASKS];
    private static int taskCount = 0;

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        showWelcome();

        String input = in.nextLine();
        while (!input.equals("bye")) {
            printLine();
            try {
                handleCommand(input);
            } catch (KiwiException e) {
                System.out.println(e.getMessage());
            }
            printLine();
            input = in.nextLine();
        }

        showGoodbye();
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
        } else if (input.equals("mark") || input.startsWith("mark ")) {
            markDone(parseTaskNumber(input, "mark"));
        } else if (input.equals("unmark") || input.startsWith("unmark ")) {
            markUndone(parseTaskNumber(input, "unmark"));
        } else {
            throw new KiwiException(
                    "Hmm, Kiwi doesn't recognize that. Try todo, deadline, event, list, mark, unmark, or bye.");
        }
    }

    /** Prints the horizontal divider used between chatbot messages. */
    private static void printLine() {
        System.out.println(LINE);
    }

    /** Shows the banner and welcome message. */
    private static void showWelcome() {
        printLine();
        System.out.print(BANNER);
        System.out.println("Hello! I'm Kiwi.");
        System.out.println("What can I do for you?");
        printLine();
    }

    /** Shows the goodbye message and exits the chat. */
    private static void showGoodbye() {
        printLine();
        System.out.println("Bye. Hope to see you again soon!");
        printLine();
    }

    /**
     * Adds a to-do if the description is present and there is list space.
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
     * Parses {@code description /by time} and adds a deadline task.
     *
     * @param body text after the {@code deadline} command
     * @throws KiwiException if the description or {@code /by} part is missing
     */
    private static void addDeadline(String body) throws KiwiException {
        if (body.isEmpty()) {
            throw new KiwiException(
                    "A deadline needs details — try: deadline return book /by Sunday");
        }
        String[] parts = body.split(" /by ", 2);
        if (parts.length < 2 || parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) {
            throw new KiwiException(
                    "Deadlines need both a description and /by <when> — e.g. deadline return book /by Sunday");
        }
        addTask(new Deadline(parts[0].trim(), parts[1].trim()));
    }

    /**
     * Parses {@code description /from start /to end} and adds an event task.
     *
     * @param body text after the {@code event} command
     * @throws KiwiException if description, {@code /from}, or {@code /to} is missing
     */
    private static void addEvent(String body) throws KiwiException {
        if (body.isEmpty()) {
            throw new KiwiException(
                    "An event needs details — try: event meeting /from Mon 2pm /to 4pm");
        }
        String[] fromSplit = body.split(" /from ", 2);
        if (fromSplit.length < 2 || fromSplit[0].trim().isEmpty()) {
            throw new KiwiException(
                    "Events need /from and /to — e.g. event meeting /from Mon 2pm /to 4pm");
        }
        String[] toSplit = fromSplit[1].split(" /to ", 2);
        if (toSplit.length < 2 || toSplit[0].trim().isEmpty() || toSplit[1].trim().isEmpty()) {
            throw new KiwiException(
                    "Events need /from and /to — e.g. event meeting /from Mon 2pm /to 4pm");
        }
        addTask(new Event(fromSplit[0].trim(), toSplit[0].trim(), toSplit[1].trim()));
    }

    /**
     * Stores a task and prints the standard "Got it" confirmation.
     *
     * @param task task to add
     * @throws KiwiException if the list is already full
     */
    private static void addTask(Task task) throws KiwiException {
        if (taskCount >= MAX_TASKS) {
            throw new KiwiException("Your task list is full (max " + MAX_TASKS + ").");
        }
        tasks[taskCount] = task;
        taskCount++;
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + taskCount + " task"
                + (taskCount == 1 ? "" : "s") + " in the list.");
    }

    /** Prints all stored tasks with 1-based numbering. */
    private static void listTasks() {
        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < taskCount; i++) {
            // Match the project example: "1.[T][ ] ..."
            System.out.println((i + 1) + "." + tasks[i]);
        }
    }

    /**
     * Reads the 1-based task number from a mark/unmark command.
     *
     * @param input   full command line
     * @param command {@code mark} or {@code unmark}
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
        if (index < 0 || index >= taskCount) {
            throw new KiwiException("There is no task number " + (index + 1) + " in your list.");
        }
        return index;
    }

    private static void markDone(int index) {
        tasks[index].markAsDone();
        System.out.println("Marked this task as done:");
        System.out.println((index + 1) + "." + tasks[index]);
    }

    private static void markUndone(int index) {
        tasks[index].markAsNotDone();
        System.out.println("Marked this task as not done yet:");
        System.out.println((index + 1) + "." + tasks[index]);
    }
}
