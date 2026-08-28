package pulbot.command;

import pulbot.storage.Storage;
import pulbot.task.TaskList;
import pulbot.ui.Ui;

public class ListCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showList(tasks);
    }
}
