package pulbot.command;

import pulbot.PulbotException;
import pulbot.storage.Storage;
import pulbot.task.Task;
import pulbot.task.TaskList;
import pulbot.ui.Ui;

/** Adds a newly created task to the task list. */
public class AddCommand extends Command {
    private final Task task;

    public AddCommand(Task task) {
        this.task = task;
    }

    @Override
    /** Adds the task, displays confirmation, and persists the updated list. */
    public void execute(TaskList tasks, Ui ui, Storage storage) throws PulbotException {
        tasks.add(task);
        ui.showAddedTask(task, tasks.size());
        storage.save(tasks);
    }
}
