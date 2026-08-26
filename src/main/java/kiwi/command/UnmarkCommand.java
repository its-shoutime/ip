package kiwi.command;

import kiwi.KiwiException;
import kiwi.Storage;
import kiwi.Ui;
import kiwi.task.TaskList;

/**
 * Marks the task at a given 0-based index as not done and saves.
 */
public class UnmarkCommand extends Command {
    private final int index;

    /**
     * @param index 0-based index of the task to unmark
     */
    public UnmarkCommand(int index) {
        this.index = index;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws KiwiException {
        if (!tasks.isValidIndex(index)) {
            throw new KiwiException("There is no task number " + (index + 1) + " in your list.");
        }
        tasks.markNotDone(index);
        storage.save(tasks.getTasks());
        ui.showUnmarked(index + 1, tasks.get(index));
    }
}
