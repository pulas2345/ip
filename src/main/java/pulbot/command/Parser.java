package pulbot.command;

import pulbot.Pulbot;
import pulbot.PulbotException;
import pulbot.task.Deadline;
import pulbot.task.Event;
import pulbot.task.Todo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/** Converts raw user input into executable commands. */
public class Parser {
    /** Parses one user input line into a command. */
    public Command parse(String input) throws PulbotException {
        if (input.equals("bye")) {
            return new ExitCommand();
        }
        if (input.equals("list")) {
            return new ListCommand();
        }
        if (startsWithCommand(input, "mark")) {
            return new MarkCommand(input.substring(4).trim());
        }
        if (startsWithCommand(input, "unmark")) {
            return new UnmarkCommand(input.substring(6).trim());
        }
        if (startsWithCommand(input, "delete")) {
            return new DeleteCommand(input.substring(6).trim());
        }
        if (startsWithCommand(input, "on")) {
            return new OnCommand(parseDate(input.substring(2).trim()));
        }
        if (startsWithCommand(input, "find")) {
            String keyword = input.substring(4).trim();
            if (keyword.isEmpty()) {
                throw new PulbotException("Please include a keyword to search for.");
            }
            return new FindCommand(keyword);
        }
        if (startsWithCommand(input, "todo")) {
            String description = input.substring(4).trim();
            if (description.isEmpty()) {
                throw new PulbotException("Please include a description.");
            }
            return new AddCommand(new Todo(description));
        }
        if (startsWithCommand(input, "deadline")) {
            String details = input.substring(8).trim();
            int byIndex = details.indexOf(" /by ");
            if (byIndex <= 0 || byIndex + 5 >= details.length()) {
                throw new PulbotException("Please use: deadline <description> /by <when>.");
            }
            return new AddCommand(new Deadline(details.substring(0, byIndex).trim(),
                    Pulbot.parseDateTime(details.substring(byIndex + 5).trim())));
        }
        if (startsWithCommand(input, "event")) {
            String details = input.substring(5).trim();
            int fromIndex = details.indexOf(" /from ");
            int toIndex = details.indexOf(" /to ", fromIndex + 7);
            if (fromIndex <= 0 || toIndex <= fromIndex + 7 || toIndex + 5 >= details.length()) {
                throw new PulbotException("Please use: event <description> /from <start> /to <end>.");
            }
            return new AddCommand(new Event(details.substring(0, fromIndex).trim(),
                    Pulbot.parseDateTime(details.substring(fromIndex + 7, toIndex).trim()),
                    Pulbot.parseDateTime(details.substring(toIndex + 5).trim())));
        }
        throw new PulbotException("Invalid command. Please read the instructions and try again.");
    }

    private boolean startsWithCommand(String input, String command) {
        return input.equals(command) || input.startsWith(command + " ");
    }

    private LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value, DateTimeFormatter.ofPattern("d/M/uuuu"));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Use d/M/yyyy for the date, for example 2/12/2019.");
        }
    }
}
