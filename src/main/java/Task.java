import java.time.LocalDate;

/**
 * Represents a task with a description, a {@link TaskType}, and a done/not-done status.
 * Subclasses may add extra details such as deadline or event times.
 */
public class Task {
    protected String description;
    protected boolean isDone;
    protected TaskType type;

    /**
     * Creates a new task that is not done yet.
     *
     * @param description what the task is about
     * @param type        whether this is a to-do, deadline, or event
     */
    public Task(String description, TaskType type) {
        this.description = description;
        this.type = type;
        this.isDone = false;
    }

    /**
     * Returns an icon showing whether the task is done.
     *
     * @return "X" if done, otherwise a space
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with X
    }

    /** Marks this task as completed. */
    public void markAsDone() {
        this.isDone = true;
    }

    /** Marks this task as not completed. */
    public void markAsNotDone() {
        this.isDone = false;
    }

    /**
     * Returns the task description.
     *
     * @return description text
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the task's type (to-do, deadline, or event).
     *
     * @return task type enum value
     */
    public TaskType getType() {
        return type;
    }

    /**
     * Returns whether this task is associated with the given calendar date.
     * Default: to-dos have no date, so they never match.
     *
     * @param date date to check
     * @return true if the task occurs on {@code date}
     */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /**
     * Returns whether this task is marked done.
     *
     * @return true if done
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns one line for hard-disk storage, e.g. {@code T | 1 | read book}.
     * Subclasses append extra fields after the description.
     *
     * @return save-file line
     */
    public String toSaveFormat() {
        return type.getIcon() + " | " + (isDone ? "1" : "0") + " | " + description;
    }

    /**
     * Returns a display string such as {@code [T][ ] read book}.
     *
     * @return formatted task text
     */
    @Override
    public String toString() {
        return type + "[" + getStatusIcon() + "] " + description;
    }
}
