package kiwi.command;

import kiwi.KiwiException;
import kiwi.Storage;
import kiwi.Ui;
import kiwi.task.TaskList;

/**
 * A user command that can be executed against the task list, UI, and storage.
 * Subclasses implement the specific behaviour for each command word.
 */
public abstract class Command {

    /**
     * Runs this command.
     *
     * @param tasks   the in-memory task list
     * @param ui      user interface for messages
     * @param storage persistence for saving after changes
     * @throws KiwiException if the command cannot be completed (e.g. bad index)
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws KiwiException;

    /**
     * Whether this command should end the chatbot loop.
     *
     * @return {@code true} only for exit; default is {@code false}
     */
    public boolean isExit() {
        return false;
    }
}
