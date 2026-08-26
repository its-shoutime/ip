package kiwi.task;

/**
 * The kind of task Kiwi can store. Each value has a one-letter display icon.
 */
public enum TaskType {
    TODO("T"),
    DEADLINE("D"),
    EVENT("E");

    private final String icon;

    TaskType(String icon) {
        this.icon = icon;
    }

    /**
     * Returns the letter shown in list output, e.g. {@code T} for a to-do.
     *
     * @return type icon letter
     */
    public String getIcon() {
        return icon;
    }

    /**
     * Returns the bracketed type label, e.g. {@code [T]}.
     *
     * @return formatted type tag
     */
    @Override
    public String toString() {
        return "[" + icon + "]";
    }
}
