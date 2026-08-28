public class MarkCommand extends Command {
    private final String taskNumber;

    public MarkCommand(String taskNumber) {
        this.taskNumber = taskNumber;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws PulbotException {
        int index = getTaskIndex(taskNumber, tasks.size());
        tasks.get(index).markAsDone();
        ui.showMarked(tasks.get(index));
        storage.save(tasks);
    }
}
