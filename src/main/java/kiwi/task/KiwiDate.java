package kiwi.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import kiwi.KiwiException;

/**
 * Shared date parsing/formatting for deadlines and events ({@code yyyy-MM-dd} in,
 * {@code MMM dd yyyy} out).
 */
public final class KiwiDate {
    /** How dates are accepted from the user and stored on disk, e.g. {@code 2019-10-15}. */
    public static final DateTimeFormatter INPUT_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    /** How dates are shown in the chatbot UI, e.g. {@code Oct 15 2019}. */
    public static final DateTimeFormatter OUTPUT_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    private KiwiDate() {
        // utility class
    }

    /**
     * Parses a {@code yyyy-MM-dd} date string.
     *
     * @param text date text such as {@code 2019-12-02}.
     * @return the parsed date.
     * @throws KiwiException If the text is not a valid ISO local date.
     */
    public static LocalDate parse(String text) throws KiwiException {
        try {
            return LocalDate.parse(text.trim(), INPUT_FORMAT);
        } catch (DateTimeParseException e) {
            throw new KiwiException("Please use a date as yyyy-MM-dd, e.g. 2019-12-02");
        }
    }

    /**
     * Formats a date for display, e.g. {@code Oct 15 2019}.
     *
     * @param date date to format.
     * @return formatted text.
     */
    public static String format(LocalDate date) {
        return date.format(OUTPUT_FORMAT);
    }
}
