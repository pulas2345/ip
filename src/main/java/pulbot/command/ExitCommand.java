package pulbot.command;

import pulbot.storage.Storage;
import pulbot.task.TaskList;
import pulbot.ui.Ui;

/** Terminates the Pulbot session. */
public class ExitCommand extends Command {
    @Override
    /** Displays the exit message. */
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showBye();
    }

    @Override
    public boolean isExit() {
        return true;
    }
}
