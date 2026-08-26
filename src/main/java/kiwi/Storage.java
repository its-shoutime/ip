package kiwi;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import kiwi.task.Deadline;
import kiwi.task.Event;
import kiwi.task.KiwiDate;
import kiwi.task.Task;
import kiwi.task.Todo;

/**
 * Saves and loads the task list from a file path supplied at construction.
 * Invalid lines are skipped with a warning so valid tasks can still be restored.
 */
public class Storage {
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
        if (Files.isDirectory(savePath)) {
            System.out.println("Could not load tasks: " + filePath
                    + " is a folder, not a file. Starting with an empty list.");
            return loaded;
        }
        if (!Files.isRegularFile(savePath)) {
            System.out.println("Could not load tasks: " + filePath
                    + " is not a normal file. Starting with an empty list.");
            return loaded;
        }
        if (!Files.isReadable(savePath)) {
            System.out.println("Could not load tasks: " + filePath
                    + " is not readable. Starting with an empty list.");
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
                task = new Deadline(description, KiwiDate.parse(by));
                break;
            case "E":
                if (parts.length != 5) {
                    throw new KiwiException(
                            "event lines must look like: E | 0 | description | yyyy-MM-dd | yyyy-MM-dd");
                }
                String fromText = parts[3].trim();
                String toText = parts[4].trim();
                if (fromText.isEmpty() || toText.isEmpty()) {
                    throw new KiwiException("event from/to values cannot be empty");
                }
                task = new Event(description, KiwiDate.parse(fromText), KiwiDate.parse(toText));
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
