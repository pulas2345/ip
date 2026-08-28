package pulbot.task;

/** Represents a task that can be stored and managed by Pulbot. */
public class Task {
    protected String description;
    protected boolean isDone;
    protected TaskType type;

    public Task(String description) {
        this(description, TaskType.TODO);
    }

    protected Task(String description, TaskType type) {
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

    @Override
    /** Returns the user-facing representation of this task. */
    public String toString() {
        return "[" + type.getIcon() + "][" + getStatusIcon() + "] " + description;
    }
}
