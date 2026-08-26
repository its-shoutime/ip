import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Saves and loads the task list from a fixed file under the project root.
 * Invalid lines are skipped with a warning so valid tasks can still be restored.
 */
public class Storage {
    /** Relative path from the project root where tasks are stored. */
    public static final String FILE_PATH = "./data/kiwi.txt";

    private static final Path SAVE_PATH = Path.of(FILE_PATH);
    private static final Path DATA_DIR = Path.of("data");
    private static final Path TEMP_PATH = Path.of("./data/kiwi.txt.tmp");

    /**
     * Writes every task to {@link #FILE_PATH}, creating {@code ./data} if needed.
     * Uses a temp file then replace so a crash mid-write is less likely to wipe the save.
     *
     * @param tasks current in-memory task list
     */
    public static void save(ArrayList<Task> tasks) {
        try {
            if (Files.exists(SAVE_PATH) && Files.isDirectory(SAVE_PATH)) {
                System.out.println("Could not save tasks: " + FILE_PATH
                        + " is a folder, not a file.");
                return;
            }
            Files.createDirectories(DATA_DIR);

            List<String> lines = new ArrayList<>();
            for (Task task : tasks) {
                lines.add(task.toSaveFormat());
            }
            Files.write(TEMP_PATH, lines, StandardCharsets.UTF_8);
            Files.move(TEMP_PATH, SAVE_PATH, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.out.println("Could not save tasks to " + FILE_PATH + ": " + e.getMessage());
            try {
                Files.deleteIfExists(TEMP_PATH);
            } catch (IOException ignored) {
                // best-effort cleanup of the temp file
            }
        }
    }

    /**
     * Reads tasks from {@link #FILE_PATH}.
     * Missing file → empty list. Unreadable path or I/O errors → empty list with a message.
     * Blank lines are ignored; corrupted lines are skipped with a warning.
     *
     * @return tasks restored from disk (may be empty)
     */
    public static ArrayList<Task> load() {
        ArrayList<Task> loaded = new ArrayList<>();

        if (!Files.exists(SAVE_PATH)) {
            return loaded;
        }
        if (Files.isDirectory(SAVE_PATH)) {
            System.out.println("Could not load tasks: " + FILE_PATH
                    + " is a folder, not a file. Starting with an empty list.");
            return loaded;
        }
        if (!Files.isRegularFile(SAVE_PATH)) {
            System.out.println("Could not load tasks: " + FILE_PATH
                    + " is not a normal file. Starting with an empty list.");
            return loaded;
        }
        if (!Files.isReadable(SAVE_PATH)) {
            System.out.println("Could not load tasks: " + FILE_PATH
                    + " is not readable. Starting with an empty list.");
            return loaded;
        }

        try {
            List<String> lines = Files.readAllLines(SAVE_PATH, StandardCharsets.UTF_8);
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
            System.out.println("Could not load tasks from " + FILE_PATH
                    + ": " + e.getMessage() + ". Starting with an empty list.");
            return new ArrayList<>();
        }
        return loaded;
    }

    /**
     * Parses one save-file line into a {@link Task}.
     * Expected forms:
     * {@code T | 1 | description},
     * {@code D | 0 | description | yyyy-MM-dd},
     * {@code E | 0 | description | from | to}.
     *
     * @param line one non-blank line from the save file
     * @return the parsed task
     * @throws KiwiException if the line is malformed
     */
    private static Task parseLine(String line) throws KiwiException {
        // Keep empty trailing fields so "D | 0 | go | " is detected as incomplete.
        String[] parts = line.split(" \\| ", -1);
        if (parts.length < 3) {
            throw new KiwiException("expected at least 3 fields separated by \" | \"");
        }

        String type = parts[0].trim();
        String doneFlag = parts[1].trim();
        String description = parts[2].trim();

        if (!doneFlag.equals("0") && !doneFlag.equals("1")) {
            throw new KiwiException("done flag must be 0 or 1, found \"" + doneFlag + "\"");
        }
        if (description.isEmpty()) {
            throw new KiwiException("description cannot be empty");
        }

        boolean isDone = doneFlag.equals("1");
        Task task;
        switch (type) {
        case "T":
            if (parts.length != 3) {
                throw new KiwiException("todo lines must look like: T | 0 | description");
            }
            task = new Todo(description);
            break;
        case "D":
            if (parts.length != 4) {
                throw new KiwiException(
                        "deadline lines must look like: D | 0 | description | yyyy-MM-dd");
            }
            String by = parts[3].trim();
            if (by.isEmpty()) {
                throw new KiwiException("deadline /by value cannot be empty");
            }
            task = new Deadline(description, Deadline.parseDate(by));
            break;
        case "E":
            if (parts.length != 5) {
                throw new KiwiException(
                        "event lines must look like: E | 0 | description | from | to");
            }
            String from = parts[3].trim();
            String to = parts[4].trim();
            if (from.isEmpty() || to.isEmpty()) {
                throw new KiwiException("event from/to values cannot be empty");
            }
            task = new Event(description, from, to);
            break;
        default:
            throw new KiwiException("unknown task type \"" + type + "\"");
        }

        if (isDone) {
            task.markAsDone();
        }
        return task;
    }
}
