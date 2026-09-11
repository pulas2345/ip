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

    /** Adds the task, displays confirmation, and persists the updated list. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws PulbotException {
        if (tasks.containsDuplicate(task)) {
            throw new PulbotException("This task already exists in your list.");
        }
        tasks.add(task);
        ui.showAddedTask(task, tasks.size());
        storage.save(tasks);
    }
}
