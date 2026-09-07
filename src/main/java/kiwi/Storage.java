package kiwi;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import kiwi.task.Deadline;
import kiwi.task.Event;
import kiwi.task.KiwiDate;
import kiwi.task.Task;
import kiwi.task.TaskType;
import kiwi.task.Todo;

/**
 * Saves and loads the task list from a file path supplied at construction.
 * Invalid lines are skipped with a warning so valid tasks can still be restored.
 */
public class Storage {
    private static final int FIELD_COUNT_MINIMUM = 3;
    private static final int FIELD_COUNT_TODO = 3;
    private static final int FIELD_COUNT_DEADLINE = 4;
    private static final int FIELD_COUNT_EVENT = 5;

    private static final int KEEP_TRAILING_EMPTY_FIELDS = -1;

    private final String filePath;
    private final Path savePath;
    private final Path dataDir;
    private final Path tempPath;

    /**
     * Creates storage that reads/writes the given file (relative or absolute).
     *
     * @param filePath path to the save file, e.g. {@code ./data/kiwi.txt}.
     */
    public Storage(String filePath) {
        assert filePath != null && !filePath.isEmpty() : "Kiwi always supplies a save-file path";
        this.filePath = filePath;
        this.savePath = Path.of(filePath);
        Path parent = savePath.getParent();
        this.dataDir = parent != null ? parent : Path.of(".");
        this.tempPath = Path.of(filePath + ".tmp");
    }

    /**
     * Writes every task to the save file, creating the parent directory if needed.
     * Uses a temp file then replace so a crash mid-write is less likely to wipe the save.
     *
     * @param tasks current in-memory task list.
     */
    public void save(ArrayList<Task> tasks) {
        assert tasks != null : "TaskList.getTasks() always returns the backing list";
        try {
            if (Files.exists(savePath) && Files.isDirectory(savePath)) {
                System.out.println("Could not save tasks: " + filePath
                        + " is a folder, not a file.");
                return;
            }
            Files.createDirectories(dataDir);

            List<String> lines = new ArrayList<>();
            for (Task task : tasks) {
                lines.add(task.toSaveFormat());
            }
            Files.write(tempPath, lines, StandardCharsets.UTF_8);
            Files.move(tempPath, savePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.out.println("Could not save tasks to " + filePath + ": " + e.getMessage());
            try {
                Files.deleteIfExists(tempPath);
            } catch (IOException ignored) {
                // best-effort cleanup of the temp file
            }
        }
    }

    /**
     * Reads tasks from the save file.
     * Missing file → empty list. Unreadable path or I/O errors → empty list with a message.
     * Blank lines are ignored; corrupted lines are skipped with a warning.
     *
     * @return tasks restored from disk (may be empty).
     */
    public ArrayList<Task> load() {
        ArrayList<Task> loaded = new ArrayList<>();

        if (!Files.exists(savePath)) {
            return loaded;
        }
        String unusableReason = unusableSaveReason();
        if (unusableReason != null) {
            System.out.println("Could not load tasks: " + unusableReason
                    + ". Starting with an empty list.");
            return loaded;
        }

        try {
            List<String> lines = Files.readAllLines(savePath, StandardCharsets.UTF_8);
            int lineNumber = 0;
            for (String raw : lines) {
                lineNumber++;
                if (raw == null || raw.isBlank()) {
                    continue;
                }
                try {
                    // stripLeading only — trailing spaces may mark an empty last field
                    String line = raw.stripLeading().replaceAll("[\\r\\n]+$", "");
                    loaded.add(parseLine(line));
                } catch (KiwiException e) {
                    System.out.println("Skipping corrupted save line " + lineNumber
                            + " (" + e.getMessage() + ")");
                }
            }
        } catch (IOException e) {
            System.out.println("Could not load tasks from " + filePath
                    + ": " + e.getMessage() + ". Starting with an empty list.");
            return new ArrayList<>();
        }
        return loaded;
    }

    /**
     * Returns why this save path cannot be loaded, or {@code null} if it can.
     */
    private String unusableSaveReason() {
        if (Files.isDirectory(savePath)) {
            return filePath + " is a folder, not a file";
        }
        if (!Files.isRegularFile(savePath)) {
            return filePath + " is not a normal file";
        }
        if (!Files.isReadable(savePath)) {
            return filePath + " is not readable";
        }
        return null;
    }

    /**
     * Parses one save-file line into a {@link Task}.
     * Expected forms:
     * {@code T | 1 | description},
     * {@code D | 0 | description | yyyy-MM-dd},
     * {@code E | 0 | description | from | to}.
     *
     * @param line one non-blank line from the save file.
     * @return the parsed task.
     * @throws KiwiException If the line is malformed.
     */
    private static Task parseLine(String line) throws KiwiException {
        assert line != null && !line.isBlank() : "load() skips blank lines before parseLine";
        // Keep empty trailing fields so "D | 0 | go | " is detected as incomplete.
        String[] parts = line.split(Pattern.quote(Task.SAVE_FIELD_SEPARATOR),
                KEEP_TRAILING_EMPTY_FIELDS);
        if (parts.length < FIELD_COUNT_MINIMUM) {
            throw new KiwiException("expected at least " + FIELD_COUNT_MINIMUM
                    + " fields separated by \"" + Task.SAVE_FIELD_SEPARATOR + "\"");
        }

        String type = parts[0].trim();
        String doneFlag = parts[1].trim();
        String description = parts[2].trim();

        boolean isDoneFlag = doneFlag.equals(Task.SAVE_DONE_FLAG);
        boolean isNotDoneFlag = doneFlag.equals(Task.SAVE_NOT_DONE_FLAG);
        if (!isDoneFlag && !isNotDoneFlag) {
            throw new KiwiException("done flag must be " + Task.SAVE_NOT_DONE_FLAG + " or "
                    + Task.SAVE_DONE_FLAG + ", found \"" + doneFlag + "\"");
        }
        if (description.isEmpty()) {
            throw new KiwiException("description cannot be empty");
        }

        Task task = createTask(TaskType.fromIcon(type), parts, description);
        if (isDoneFlag) {
            task.markAsDone();
        }
        return task;
    }

    private static Task createTask(TaskType taskType, String[] parts, String description)
            throws KiwiException {
        switch (taskType) {
            case TODO:
                return parseTodoLine(parts, description);
            case DEADLINE:
                return parseDeadlineLine(parts, description);
            case EVENT:
                return parseEventLine(parts, description);
            default:
                throw new KiwiException("unknown task type \"" + taskType.getIcon() + "\"");
        }
    }

    private static Task parseTodoLine(String[] parts, String description) throws KiwiException {
        if (parts.length != FIELD_COUNT_TODO) {
            throw new KiwiException("todo lines must look like: T | 0 | description");
        }
        return new Todo(description);
    }

    private static Task parseDeadlineLine(String[] parts, String description) throws KiwiException {
        if (parts.length != FIELD_COUNT_DEADLINE) {
            throw new KiwiException(
                    "deadline lines must look like: D | 0 | description | yyyy-MM-dd");
        }
        String by = parts[3].trim();
        if (by.isEmpty()) {
            throw new KiwiException("deadline /by value cannot be empty");
        }
        return new Deadline(description, KiwiDate.parse(by));
    }

    private static Task parseEventLine(String[] parts, String description) throws KiwiException {
        if (parts.length != FIELD_COUNT_EVENT) {
            throw new KiwiException(
                    "event lines must look like: E | 0 | description | yyyy-MM-dd | yyyy-MM-dd");
        }
        String fromText = parts[3].trim();
        String toText = parts[4].trim();
        if (fromText.isEmpty() || toText.isEmpty()) {
            throw new KiwiException("event from/to values cannot be empty");
        }
        return new Event(description, KiwiDate.parse(fromText), KiwiDate.parse(toText));
    }
}
