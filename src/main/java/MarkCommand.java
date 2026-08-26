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
