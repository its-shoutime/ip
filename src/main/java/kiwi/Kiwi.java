package kiwi;

import kiwi.command.Command;
import kiwi.command.ExitCommand;
import kiwi.task.TaskList;

/**
 * Kiwi is a simple chatbot that stores to-dos, deadlines, and events in memory,
 * lists them, and can mark, unmark, or delete them.
 * <p>
 * This class wires {@link Ui}, {@link Parser}, {@link TaskList}, and {@link Storage}
 * together. Each user line becomes a {@link Command} that executes itself.
 */
public class Kiwi {
    /** Default save file used when launching from {@link #main(String[])}. */
    public static final String DEFAULT_FILE_PATH = "./data/kiwi.txt";

    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;

    /**
     * Creates a Kiwi chatbot that loads tasks from {@code filePath}.
     *
     * @param filePath path to the task save file.
     */
    public Kiwi(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        tasks = new TaskList(storage.load());
    }

    /**
     * Runs the chatbot loop until an {@link ExitCommand} is executed.
     */
    public void run() {
        ui.showWelcome();
        boolean isExit = false;
        while (!isExit) {
            try {
                String fullCommand = ui.readCommand();
                ui.showLine();
                Command command = Parser.parse(fullCommand);
                command.execute(tasks, ui, storage);
                isExit = command.isExit();
            } catch (KiwiException e) {
                ui.showError(e.getMessage());
            } finally {
                ui.showLine();
            }
        }
    }

    /**
     * Starts Kiwi using the default save file {@link #DEFAULT_FILE_PATH}.
     *
     * @param args command-line arguments (unused).
     */
    public static void main(String[] args) {
        new Kiwi(DEFAULT_FILE_PATH).run();
    }
}
