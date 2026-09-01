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
    private final StringBuilder response = new StringBuilder();

    /** Creates a UI that reads from standard input. */
    public Ui() {
        this.in = new Scanner(System.in);
    }

    /**
     * Prints each of {@code messages} to the console and records them for {@link #consumeResponse()}.
     *
     * @param messages one or more lines of chatbot output.
     */
    private void show(String... messages) {
        for (String message : messages) {
            System.out.println(message);
            if (response.length() > 0) {
                response.append('\n');
            }
            response.append(message);
        }
    }

    /**
     * Returns messages shown since the last consume, then clears the buffer.
     * Used by the GUI so a dialog box can display the same text the console would print.
     *
     * @return the accumulated reply, or an empty string if nothing was shown.
     */
    public String consumeResponse() {
        String result = response.toString();
        response.setLength(0);
        return result;
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
        show("Bye. Hope to see you again soon!");
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
        show(message);
    }

    /**
     * Confirms that a task was added and reports the new list size.
     *
     * @param task added task.
     * @param size number of tasks after the add.
     */
    public void showTaskAdded(Task task, int size) {
        show("Got it. I've added this task:",
                "  " + task,
                "Now you have " + size + " task"
                        + (size == 1 ? "" : "s") + " in the list.");
    }

    /**
     * Prints all tasks with 1-based numbering.
     *
     * @param tasks current task list.
     */
    public void showTaskList(TaskList tasks) {
        show("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            show((i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Prints tasks whose descriptions matched a {@code find} keyword, numbered from 1.
     * Prints {@code None found.} when the filtered list is empty.
     *
     * @param matches tasks whose descriptions contain the search keyword
     */
    public void showMatchingTasks(TaskList matches) {
        show("Here are the matching tasks in your list:");
        for (int i = 0; i < matches.size(); i++) {
            show((i + 1) + "." + matches.get(i));
        }
        if (matches.size() == 0) {
            show("None found.");
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
        show("Here are the deadlines/events on " + KiwiDate.format(date) + ":");
        int shown = 0;
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (task.occursOn(date)) {
                show((i + 1) + "." + task);
                shown++;
            }
        }
        if (shown == 0) {
            show("None found.");
        }
    }

    /**
     * Confirms a task was marked done.
     *
     * @param displayNumber 1-based task number shown to the user.
     * @param task the marked task.
     */
    public void showMarked(int displayNumber, Task task) {
        show("Marked this task as done:",
                displayNumber + "." + task);
    }

    /**
     * Confirms a task was marked not done.
     *
     * @param displayNumber 1-based task number shown to the user.
     * @param task the unmarked task.
     */
    public void showUnmarked(int displayNumber, Task task) {
        show("Marked this task as not done yet:",
                displayNumber + "." + task);
    }

    /**
     * Confirms a task was removed and reports the new list size.
     *
     * @param removed the removed task.
     * @param size number of tasks after the delete.
     */
    public void showTaskDeleted(Task removed, int size) {
        show("Noted. I've removed this task:",
                "  " + removed,
                "Now you have " + size + " task"
                        + (size == 1 ? "" : "s") + " in the list.");
    }
}
