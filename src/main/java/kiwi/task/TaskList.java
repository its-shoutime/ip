package kiwi.task;

import java.util.ArrayList;

/**
 * Holds the in-memory list of tasks and supports add, delete, get, and mark operations.
 * Does not print messages or save to disk — callers handle UI and persistence.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list that wraps an existing list (e.g. loaded from storage).
     *
     * @param tasks tasks to manage; must not be {@code null}
     */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task task to add
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Removes and returns the task at the given 0-based index.
     *
     * @param index 0-based position of the task to remove
     * @return the removed task
     */
    public Task delete(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns the task at the given 0-based index.
     *
     * @param index 0-based position
     * @return the task at that position
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Returns how many tasks are in the list.
     *
     * @return current size
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the underlying list for saving or display.
     *
     * @return the mutable list of tasks
     */
    public ArrayList<Task> getTasks() {
        return tasks;
    }

    /**
     * Marks the task at {@code index} as done.
     *
     * @param index 0-based position
     */
    public void markDone(int index) {
        tasks.get(index).markAsDone();
    }

    /**
     * Marks the task at {@code index} as not done.
     *
     * @param index 0-based position
     */
    public void markNotDone(int index) {
        tasks.get(index).markAsNotDone();
    }

    /**
     * Checks whether {@code index} is a valid 0-based position in this list.
     *
     * @param index candidate index
     * @return {@code true} if the index is in range
     */
    public boolean isValidIndex(int index) {
        return index >= 0 && index < tasks.size();
    }
}
