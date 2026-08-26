import java.time.LocalDate;

/**
 * Lists deadlines/events that occur on a given date.
 */
public class OnCommand extends Command {
    private final LocalDate date;

    /**
     * @param date date to filter by
     */
    public OnCommand(LocalDate date) {
        this.date = date;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTasksOn(date, tasks);
    }
}
