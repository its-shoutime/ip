package kiwi;

import java.time.LocalDate;
import java.util.Scanner;

import kiwi.task.KiwiDate;
import kiwi.task.Task;
import kiwi.task.TaskList;

/**
 * Handles all interactions with the user: reading commands and printing messages.
 * Keeps console I/O out of the rest of the application.
 */
public class Ui {
    private static final String LINE = "____________________________________________________________";

    private static final String BANNER = " _  ___          _ \n"
            + "| |/ (_)_      _(_)\n"
            + "| ' /| \\ \\ /\\ / / |\n"
            + "| . \\| |\\ V  V /| |\n"
            + "|_|\\_\\_| \\_/\\_/ |_|\n";

    private final Scanner in;

    /** Creates a UI that reads from standard input. */
    public Ui() {
        this.in = new Scanner(System.in);
    }

    /** Prints the horizontal divider used between chatbot messages. */
    public void showLine() {
        System.out.println(LINE);
    }

    /** Shows the banner and welcome message. */
    public void showWelcome() {
        showLine();
        System.out.print(BANNER);
        System.out.println("Hello! I'm Kiwi.");
        System.out.println("What can I do for you?");
        showLine();
    }

    /**
     * Shows the goodbye message only (no divider lines).
     * The main loop prints the dividers around each command, including exit.
     */
    public void showGoodbye() {
        System.out.println("Bye. Hope to see you again soon!");
    }

    /**
     * Reads the next full line of user input.
     *
     * @return the command line typed by the user.
     */
    public String readCommand() {
        return in.nextLine();
    }

    /**
     * Shows an error message (typically from a {@link KiwiException}).
     *
     * @param message text to display.
     */
    public void showError(String message) {
        System.out.println(message);
    }

    /**
     * Confirms that a task was added and reports the new list size.
     *
     * @param task added task.
     * @param size number of tasks after the add.
     */
    public void showTaskAdded(Task task, int size) {
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + size + " task"
                + (size == 1 ? "" : "s") + " in the list.");
    }

    /**
     * Prints all tasks with 1-based numbering.
     *
     * @param tasks current task list.
     */
    public void showTaskList(TaskList tasks) {
        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Prints deadlines/events that fall on {@code date}, or "None found."
     * Uses each task's original 1-based index in the full list.
     *
     * @param date date being queried.
     * @param tasks full task list.
     */
    public void showTasksOn(LocalDate date, TaskList tasks) {
        System.out.println("Here are the deadlines/events on " + KiwiDate.format(date) + ":");
        int shown = 0;
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (task.occursOn(date)) {
                System.out.println((i + 1) + "." + task);
                shown++;
            }
        }
        if (shown == 0) {
            System.out.println("None found.");
        }
    }

    /**
     * Confirms a task was marked done.
     *
     * @param displayNumber 1-based task number shown to the user.
     * @param task the marked task.
     */
    public void showMarked(int displayNumber, Task task) {
        System.out.println("Marked this task as done:");
        System.out.println(displayNumber + "." + task);
    }

    /**
     * Confirms a task was marked not done.
     *
     * @param displayNumber 1-based task number shown to the user.
     * @param task the unmarked task.
     */
    public void showUnmarked(int displayNumber, Task task) {
        System.out.println("Marked this task as not done yet:");
        System.out.println(displayNumber + "." + task);
    }

    /**
     * Confirms a task was removed and reports the new list size.
     *
     * @param removed the removed task.
     * @param size number of tasks after the delete.
     */
    public void showTaskDeleted(Task removed, int size) {
        System.out.println("Noted. I've removed this task:");
        System.out.println("  " + removed);
        System.out.println("Now you have " + size + " task"
                + (size == 1 ? "" : "s") + " in the list.");
    }
}
