/**
 * Represents a task with a description and a done/not-done status.
 */
public class Task {
    protected String description;
    protected boolean isDone;

    /**
     * Creates a new task that is not done yet.
     *
     * @param description what the task is about
     */
    public Task(String description) {
        this.description = description;
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
     * Returns a display string such as "[X] read book".
     *
     * @return formatted task text
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
