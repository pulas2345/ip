package pulbot.task;

import pulbot.Pulbot;

import java.time.LocalDateTime;

/** Represents a task that must be completed by a specified date and time. */
public class Deadline extends Task {
    protected LocalDateTime by;

    public Deadline(String description, String by) {
        this(description, Pulbot.parseDateTime(by));
    }

    public Deadline(String description, LocalDateTime by) {
        super(description, TaskType.DEADLINE);
        this.by = by;
    }

    /** Returns the deadline date and time. */
    public LocalDateTime getBy() {
        return by;
    }

    @Override
    /** Returns the user-facing representation including the deadline. */
    public String toString() {
        return super.toString() + " (by: " + Pulbot.formatDateTime(by) + ")";
    }
}
