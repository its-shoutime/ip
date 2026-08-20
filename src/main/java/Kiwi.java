import java.util.Scanner;

public class Kiwi {
    private static final String LINE = "____________________________________________________________";

    private static final String BANNER = " _  ___          _ \n"
            + "| |/ (_)_      _(_)\n"
            + "| ' /| \\ \\ /\\ / / |\n"
            + "| . \\| |\\ V  V /| |\n"
            + "|_|\\_\\_| \\_/\\_/ |_|\n";

    private static final String[] tasks = new String[100];
    private static final boolean[] taskDone = new boolean[100];
    private static int taskCount = 0;

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        showWelcome();

        String input = in.nextLine();
        while (!input.equals("bye")) {
            printLine();
            if (input.equals("list")) {
                listTasks();
            } else if (input.startsWith("mark")) {
                markDone(Integer.parseInt(input.split(" ")[1]) - 1);
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
        tasks[taskCount] = description;
        taskDone[taskCount] = false;
        taskCount++;
        System.out.println("added: " + description);
    }

    /** Prints all stored tasks with 1-based numbering. */
    private static void listTasks() {
        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < taskCount; i++) {
            String checkbox = taskDone[i] ? "[X]" : "[ ]";
            System.out.println((i + 1) + ". " + checkbox + " " + tasks[i]);
        }
    }

    private static void markDone(int index) {
        taskDone[index] = true;
        System.out.println("Marked this task as done:");
        System.out.println((index + 1) + ". [X] " + tasks[index]);
    }

}
