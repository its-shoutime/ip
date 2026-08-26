/**
 * Adds a task (to-do, deadline, or event) to the list and saves.
 */
public class AddCommand extends Command {
    private final Task task;

    /**
     * @param task the task to add (already parsed)
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        tasks.add(task);
        storage.save(tasks.getTasks());
        ui.showTaskAdded(task, tasks.size());
    }
}
