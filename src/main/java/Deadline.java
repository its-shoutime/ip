import java.time.LocalDate;

/**
 * A deadline task that must be done before a given calendar date.
 */
public class Deadline extends Task {
    private final LocalDate by;

    /**
     * Creates a deadline task.
     *
     * @param description what needs to be done
     * @param by         due date
     */
    public Deadline(String description, LocalDate by) {
        super(description, TaskType.DEADLINE);
        this.by = by;
    }

    /**
     * Returns the due date.
     *
     * @return due date
     */
    public LocalDate getBy() {
        return by;
    }

    @Override
    public boolean occursOn(LocalDate date) {
        return by.equals(date);
    }

    @Override
    public String toSaveFormat() {
        // Keep ISO on disk so load can parse reliably.
        return super.toSaveFormat() + " | " + by.format(KiwiDate.INPUT_FORMAT);
    }

    @Override
    public String toString() {
        return super.toString() + " (by: " + KiwiDate.format(by) + ")";
    }
}
