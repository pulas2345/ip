package pulbot.command;

import pulbot.storage.Storage;
import pulbot.task.TaskList;
import pulbot.ui.Ui;

import java.time.LocalDate;

public class OnCommand extends Command {
    private final LocalDate date;

    public OnCommand(LocalDate date) {
        this.date = date;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTasksOnDate(tasks, date);
    }
}
