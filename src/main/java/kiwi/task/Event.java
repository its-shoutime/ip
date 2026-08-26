package kiwi.task;

import java.time.LocalDate;
import kiwi.KiwiException;

/**
 * An event task with a start and end calendar date (inclusive range).
 */
public class Event extends Task {
    private final LocalDate from;
    private final LocalDate to;

    /**
     * Creates an event task.
     *
     * @param description what the event is
     * @param from        start date (inclusive)
     * @param to          end date (inclusive)
     * @throws KiwiException if {@code to} is before {@code from}
     */
    public Event(String description, LocalDate from, LocalDate to) throws KiwiException {
        super(description, TaskType.EVENT);
        if (to.isBefore(from)) {
            throw new KiwiException("Event end date cannot be before the start date.");
        }
        this.from = from;
        this.to = to;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@code true} if {@code date} falls in this event's inclusive from–to range
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return !date.isBefore(from) && !date.isAfter(to);
    }

    /**
     * {@inheritDoc}
     * Appends the start and end dates in {@code yyyy-MM-dd} form so they can be loaded again.
     */
    @Override
    public String toSaveFormat() {
        return super.toSaveFormat() + " | " + from.format(KiwiDate.INPUT_FORMAT)
                + " | " + to.format(KiwiDate.INPUT_FORMAT);
    }

    /**
     * {@inheritDoc}
     * Appends {@code (from: MMM dd yyyy to: MMM dd yyyy)} for display.
     */
    @Override
    public String toString() {
        return super.toString() + " (from: " + KiwiDate.format(from)
                + " to: " + KiwiDate.format(to) + ")";
    }
}
