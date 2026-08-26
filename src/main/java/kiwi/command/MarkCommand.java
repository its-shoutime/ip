package kiwi.command;

import kiwi.KiwiException;
import kiwi.Storage;
import kiwi.Ui;
import kiwi.task.TaskList;

/**
 * Marks the task at a given 0-based index as done and saves.
 */
public class MarkCommand extends Command {
    private final int index;

    /**
     * @param index 0-based index of the task to mark done
     */
    public MarkCommand(int index) {
        this.index = index;
    }

    /**
     * Marks the task done, saves the list, and shows a confirmation.
     *
     * {@inheritDoc}
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws KiwiException {
        if (!tasks.isValidIndex(index)) {
            throw new KiwiException("There is no task number " + (index + 1) + " in your list.");
        }
        tasks.markDone(index);
        storage.save(tasks.getTasks());
        ui.showMarked(index + 1, tasks.get(index));
    }
}
