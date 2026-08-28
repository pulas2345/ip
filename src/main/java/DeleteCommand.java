public class DeleteCommand extends Command {
    private final String taskNumber;

    public DeleteCommand(String taskNumber) {
        this.taskNumber = taskNumber;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws PulbotException {
        int index = getTaskIndex(taskNumber, tasks.size());
        Task removedTask = tasks.remove(index);
        ui.showDeleted(removedTask, tasks.size());
        storage.save(tasks);
    }
}
