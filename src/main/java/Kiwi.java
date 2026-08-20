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
            if (input.equals("list")) {
                listTasks();
            } else if (input.startsWith("mark ")) {
                markDone(Integer.parseInt(input.split(" ")[1]) - 1);
            } else if (input.startsWith("unmark ")) {
                markUndone(Integer.parseInt(input.split(" ")[1]) - 1);
            } else if (input.startsWith("todo ")) {
                addTodo(input.substring("todo ".length()).trim());
            } else if (input.startsWith("deadline ")) {
                addDeadline(input.substring("deadline ".length()).trim());
            } else if (input.startsWith("event ")) {
                addEvent(input.substring("event ".length()).trim());
            } else {
                System.out.println("I don't understand that command.");
            }
            printLine();
            input = in.nextLine();
        }

        showGoodbye();
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
     * Adds a to-do if there is still space in the list.
     *
     * @param description task description after the {@code todo} command
     */
    private static void addTodo(String description) {
        addTask(new Todo(description));
    }

    /**
     * Parses {@code description /by time} and adds a deadline task.
     *
     * @param body text after the {@code deadline} command
     */
    private static void addDeadline(String body) {
        String[] parts = body.split(" /by ", 2);
        if (parts.length < 2) {
            System.out.println("Please use: deadline <description> /by <when>");
            return;
        }
        addTask(new Deadline(parts[0].trim(), parts[1].trim()));
    }

    /**
     * Parses {@code description /from start /to end} and adds an event task.
     *
     * @param body text after the {@code event} command
     */
    private static void addEvent(String body) {
        String[] fromSplit = body.split(" /from ", 2);
        if (fromSplit.length < 2) {
            System.out.println("Please use: event <description> /from <start> /to <end>");
            return;
        }
        String[] toSplit = fromSplit[1].split(" /to ", 2);
        if (toSplit.length < 2) {
            System.out.println("Please use: event <description> /from <start> /to <end>");
            return;
        }
        addTask(new Event(fromSplit[0].trim(), toSplit[0].trim(), toSplit[1].trim()));
    }

    /**
     * Stores a task and prints the standard "Got it" confirmation.
     *
     * @param task task to add
     */
    private static void addTask(Task task) {
        if (taskCount >= MAX_TASKS) {
            System.out.println("Cannot add more tasks. The list is full.");
            return;
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
