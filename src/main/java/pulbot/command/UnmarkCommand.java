package pulbot.command;

import pulbot.PulbotException;
import pulbot.storage.Storage;
import pulbot.task.TaskList;
import pulbot.ui.Ui;

public class UnmarkCommand extends Command {
    private final String taskNumber;

    public UnmarkCommand(String taskNumber) {
        this.taskNumber = taskNumber;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws PulbotException {
        int index = getTaskIndex(taskNumber, tasks.size());
        tasks.get(index).markAsNotDone();
        ui.showUnmarked(tasks.get(index));
        storage.save(tasks);
    }
}
