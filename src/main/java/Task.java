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
     * Returns a display string such as {@code [T][ ] read book}.
     *
     * @return formatted task text
     */
    @Override
    public String toString() {
        return type + "[" + getStatusIcon() + "] " + description;
    }
}
