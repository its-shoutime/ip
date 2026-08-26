import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * A deadline task that must be done before a given calendar date.
 */
public class Deadline extends Task {
    /** How dates are accepted from the user and stored on disk, e.g. {@code 2019-10-15}. */
    public static final DateTimeFormatter INPUT_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    /** How dates are shown in the chatbot UI, e.g. {@code Oct 15 2019}. */
    public static final DateTimeFormatter OUTPUT_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

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
     * Parses a user/save-file date in {@code yyyy-MM-dd} form.
     *
     * @param text date text such as {@code 2019-12-02}
     * @return the parsed date
     * @throws KiwiException if the text is not a valid {@code yyyy-MM-dd} date
     */
    public static LocalDate parseDate(String text) throws KiwiException {
        try {
            return LocalDate.parse(text.trim(), INPUT_FORMAT);
        } catch (DateTimeParseException e) {
            throw new KiwiException(
                    "Please give the deadline as yyyy-MM-dd, e.g. deadline return book /by 2019-12-02");
        }
    }

    /**
     * Returns the due date for display, e.g. {@code Oct 15 2019}.
     *
     * @return formatted due date
     */
    public String getFormattedBy() {
        return by.format(OUTPUT_FORMAT);
    }

    @Override
    public String toSaveFormat() {
        // Keep ISO on disk so load can parse reliably.
        return super.toSaveFormat() + " | " + by.format(INPUT_FORMAT);
    }

    @Override
    public String toString() {
        return super.toString() + " (by: " + getFormattedBy() + ")";
    }
}
