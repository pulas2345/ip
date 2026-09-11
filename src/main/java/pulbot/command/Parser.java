package pulbot.command;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import pulbot.Pulbot;
import pulbot.PulbotException;
import pulbot.task.Deadline;
import pulbot.task.Event;
import pulbot.task.Todo;

/** Converts raw user input into executable commands. */
public class Parser {
    private static final String DEADLINE_SEPARATOR = " /by ";
    private static final String EVENT_FROM_SEPARATOR = " /from ";
    private static final String EVENT_TO_SEPARATOR = " /to ";
    private static final String INVALID_COMMAND_MESSAGE =
            "Invalid command. Please read the instructions and try again.";
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("d/M/uuuu").withResolverStyle(ResolverStyle.STRICT);

    /** Parses one user input line into a command. */
    public Command parse(String input) throws PulbotException {
        String commandWord = getCommandWord(input);
        switch (commandWord) {
            case "bye":
                requireExactCommand(input, commandWord);
                return new ExitCommand();
            case "list":
                requireExactCommand(input, commandWord);
                return new ListCommand();
            case "mark":
                return new MarkCommand(getArguments(input, commandWord));
            case "unmark":
                return new UnmarkCommand(getArguments(input, commandWord));
            case "delete":
                return new DeleteCommand(getArguments(input, commandWord));
            case "on":
                return new OnCommand(parseDate(getArguments(input, commandWord)));
            case "find":
                return parseFind(getArguments(input, commandWord));
            case "todo":
                return parseTodo(getArguments(input, commandWord));
            case "deadline":
                return parseDeadline(getArguments(input, commandWord));
            case "event":
                return parseEvent(getArguments(input, commandWord));
            default:
                throw new PulbotException(INVALID_COMMAND_MESSAGE);
        }
    }

    private String getCommandWord(String input) {
        int firstSpaceIndex = input.indexOf(' ');
        if (firstSpaceIndex < 0) {
            return input;
        }
        return input.substring(0, firstSpaceIndex);
    }

    private String getArguments(String input, String commandWord) {
        return input.substring(commandWord.length()).trim();
    }

    private void requireExactCommand(String input, String commandWord) throws PulbotException {
        if (!input.equals(commandWord)) {
            throw new PulbotException(INVALID_COMMAND_MESSAGE);
        }
    }

    private Command parseFind(String keyword) throws PulbotException {
        if (keyword.isEmpty()) {
            throw new PulbotException("Please include a keyword to search for.");
        }
        return new FindCommand(keyword);
    }

    private Command parseTodo(String description) throws PulbotException {
        if (description.isEmpty()) {
            throw new PulbotException("Please include a description.");
        }
        return new AddCommand(new Todo(description));
    }

    /** Parses and validates a deadline's description and date-time arguments. */
    private Command parseDeadline(String details) throws PulbotException {
        int separatorIndex = details.indexOf(DEADLINE_SEPARATOR);
        boolean hasDescription = separatorIndex > 0;
        boolean hasDateTime = separatorIndex + DEADLINE_SEPARATOR.length() < details.length();
        if (!hasDescription || !hasDateTime) {
            throw new PulbotException("Please use: deadline <description> /by <when>.");
        }

        String description = details.substring(0, separatorIndex).trim();
        String dateTime = details.substring(separatorIndex + DEADLINE_SEPARATOR.length()).trim();
        return new AddCommand(new Deadline(description, Pulbot.parseDateTime(dateTime)));
    }

    /** Parses and validates an event's description, start time, and end time. */
    private Command parseEvent(String details) throws PulbotException {
        int fromIndex = details.indexOf(EVENT_FROM_SEPARATOR);
        int toIndex = details.indexOf(EVENT_TO_SEPARATOR,
                fromIndex + EVENT_FROM_SEPARATOR.length());
        boolean hasDescription = fromIndex > 0;
        boolean hasStartTime = toIndex > fromIndex + EVENT_FROM_SEPARATOR.length();
        boolean hasEndTime = toIndex + EVENT_TO_SEPARATOR.length() < details.length();
        if (!hasDescription || !hasStartTime || !hasEndTime) {
            throw new PulbotException("Please use: event <description> /from <start> /to <end>.");
        }

        String description = details.substring(0, fromIndex).trim();
        String startTime = details.substring(fromIndex + EVENT_FROM_SEPARATOR.length(), toIndex).trim();
        String endTime = details.substring(toIndex + EVENT_TO_SEPARATOR.length()).trim();
        return new AddCommand(new Event(description,
                Pulbot.parseDateTime(startTime), Pulbot.parseDateTime(endTime)));
    }

    private LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Use d/M/yyyy for the date, for example 2/12/2019.");
        }
    }
}
