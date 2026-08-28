package pulbot.task;

import pulbot.Pulbot;

import java.time.LocalDateTime;

public class Event extends Task {
    protected LocalDateTime from;
    protected LocalDateTime to;

    public Event(String description, String from, String to) {
        this(description, Pulbot.parseDateTime(from), Pulbot.parseDateTime(to));
    }

    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description, TaskType.EVENT);
        this.from = from;
        this.to = to;
    }

    public LocalDateTime getFrom() {
        return from;
    }

    public LocalDateTime getTo() {
        return to;
    }

    @Override
    public String toString() {
        return super.toString() + " (from: " + Pulbot.formatDateTime(from)
                + " to: " + Pulbot.formatDateTime(to) + ")";
    }
}
