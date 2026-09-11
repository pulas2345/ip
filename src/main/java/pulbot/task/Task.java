package pulbot.task;

/** Represents a task that can be stored and managed by Pulbot. */
public class Task {
    private final String description;
    private final TaskType type;
    private boolean isDone;

    public Task(String description) {
        this(description, TaskType.TODO);
    }

    protected Task(String description, TaskType type) {
        assert description != null && !description.isBlank()
                : "Tasks must have a non-blank description after input validation";
        assert type != null : "Every task must have a type";
        this.description = description;
        this.isDone = false;
        this.type = type;
    }

    /** Returns the completion icon for this task. */
    public String getStatusIcon() {
        return isDone ? "\u2713" : " "; // mark done task with tick
    }

    /** Marks this task as completed. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as incomplete. */
    public void markAsNotDone() {
        isDone = false;
    }

    /** Returns the task description. */
    public String getDescription() {
        return description;
    }

    /** Returns this task's type. */
    public TaskType getType() {
        return type;
    }

    /** Returns whether this task is completed. */
    public Boolean isDone() {
        return isDone;
    }

    /** Returns the user-facing representation of this task. */
    @Override
    public String toString() {
        return "[" + type.getIcon() + "][" + getStatusIcon() + "] " + description;
    }
}
