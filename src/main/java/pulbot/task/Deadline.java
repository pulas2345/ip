package pulbot.task;

import pulbot.Pulbot;

import java.time.LocalDateTime;

public class Deadline extends Task {
    protected LocalDateTime by;

    public Deadline(String description, String by) {
        this(description, Pulbot.parseDateTime(by));
    }

    public Deadline(String description, LocalDateTime by) {
        super(description, TaskType.DEADLINE);
        this.by = by;
    }

    public LocalDateTime getBy() {
        return by;
    }

    @Override
    public String toString() {
        return super.toString() + " (by: " + Pulbot.formatDateTime(by) + ")";
    }
}
