import java.util.Scanner;

/**
 * Kiwi is a simple chatbot that stores tasks in memory and can list them.
 */
public class Kiwi {
    private static final String LINE = "____________________________________________________________";
    private static final int MAX_TASKS = 100;

    private static final String BANNER = " _  ___          _ \n"
            + "| |/ (_)_      _(_)\n"
            + "| ' /| \\ \\ /\\ / / |\n"
            + "| . \\| |\\ V  V /| |\n"
            + "|_|\\_\\_| \\_/\\_/ |_|\n";

    private static final String[] tasks = new String[MAX_TASKS];
    private static int taskCount = 0;

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        showWelcome();

        String input = in.nextLine();
        while (!input.equals("bye")) {
            printLine();
            if (input.equals("list")) {
                listTasks();
            } else {
                addTask(input);
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
     * Stores a new task if there is still space in the fixed-size list.
     *
     * @param description text entered by the user
     */
    private static void addTask(String description) {
        if (taskCount >= MAX_TASKS) {
            System.out.println("Cannot add more tasks. The list is full.");
            return;
        }
        tasks[taskCount] = description;
        taskCount++;
        System.out.println("added: " + description);
    }

    /** Prints all stored tasks with 1-based numbering. */
    private static void listTasks() {
        for (int i = 0; i < taskCount; i++) {
            System.out.println((i + 1) + ". " + tasks[i]);
        }
    }
}
