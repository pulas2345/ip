/** A user command that can be executed against Pulbot's collaborators. */
public abstract class Command {
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws PulbotException;

    public boolean isExit() {
        return false;
    }

    protected int getTaskIndex(String number, int taskCount) throws PulbotException {
        try {
            int index = Integer.parseInt(number) - 1;
            if (index < 0 || index >= taskCount) {
                throw new PulbotException("There is no task with that number. Please enter a valid task number.");
            }
            return index;
        } catch (NumberFormatException e) {
            throw new PulbotException("Please enter a valid task number.");
        }
    }
}
