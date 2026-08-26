package kiwi.command;

import kiwi.Storage;
import kiwi.Ui;
import kiwi.task.Task;
import kiwi.task.TaskList;

/**
 * Adds a task (to-do, deadline, or event) to the list and saves.
 */
public class AddCommand extends Command {
    private final Task task;

    /**
     * Creates a command that adds the given task.
     *
     * @param task the task to add (already parsed).
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    /**
     * Adds the task, saves the list, and shows a confirmation.
     *
     * {@inheritDoc}
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        tasks.add(task);
        storage.save(tasks.getTasks());
        ui.showTaskAdded(task, tasks.size());
    }
}
