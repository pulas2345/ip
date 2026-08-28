package pulbot.command;

import pulbot.PulbotException;
import pulbot.storage.Storage;
import pulbot.task.TaskList;
import pulbot.ui.Ui;

/** Marks a task selected by its displayed number as complete. */
public class MarkCommand extends Command {
    private final String taskNumber;

    public MarkCommand(String taskNumber) {
        this.taskNumber = taskNumber;
    }

    @Override
    /** Marks the selected task and persists the updated list. */
    public void execute(TaskList tasks, Ui ui, Storage storage) throws PulbotException {
        int index = getTaskIndex(taskNumber, tasks.size());
        tasks.get(index).markAsDone();
        ui.showMarked(tasks.get(index));
        storage.save(tasks);
    }
}
