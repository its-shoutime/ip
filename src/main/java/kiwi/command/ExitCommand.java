package kiwi.command;

import kiwi.Storage;
import kiwi.Ui;
import kiwi.task.TaskList;

/**
 * Ends the chatbot session and shows the goodbye message.
 */
public class ExitCommand extends Command {

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    @Override
    public boolean isExit() {
        return true;
    }
}
