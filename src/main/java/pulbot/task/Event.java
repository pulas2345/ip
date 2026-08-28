package pulbot.task;

import pulbot.Pulbot;

import java.time.LocalDateTime;

/** Represents a task that takes place during a date and time range. */
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

    /** Returns the event start date and time. */
    public LocalDateTime getFrom() {
        return from;
    }

    /** Returns the event end date and time. */
    public LocalDateTime getTo() {
        return to;
    }

    @Override
    /** Returns the user-facing representation including the event range. */
    public String toString() {
        return super.toString() + " (from: " + Pulbot.formatDateTime(from)
                + " to: " + Pulbot.formatDateTime(to) + ")";
    }
}
