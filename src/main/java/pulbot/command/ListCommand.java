package pulbot.command;

import pulbot.storage.Storage;
import pulbot.task.TaskList;
import pulbot.ui.Ui;

/** Displays all tasks in their current order. */
public class ListCommand extends Command {
    @Override
    /** Displays the current task list. */
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showList(tasks);
    }
}
