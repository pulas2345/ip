package pulbot.storage;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

import pulbot.PulbotException;
import pulbot.task.Deadline;
import pulbot.task.Event;
import pulbot.task.Task;
import pulbot.task.TaskList;
import pulbot.task.TaskType;
import pulbot.task.Todo;

/** Handles loading tasks from and saving tasks to a file. */
public class Storage {
    private static final DateTimeFormatter STORED_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd uuuu h:mm a", Locale.ENGLISH)
                    .withResolverStyle(ResolverStyle.STRICT);
    private final Path filePath;

    /** Creates storage backed by the supplied file path. */
    public Storage(String filePath) {
        this.filePath = Path.of(filePath);
    }

    /** Loads tasks from disk, returning an empty list when the file is absent. */
    public TaskList load() throws PulbotException {
        TaskList tasks = new TaskList();
        if (!Files.exists(filePath)) {
            return tasks;
        }
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                tasks.add(parseTask(line));
            }
        } catch (IllegalArgumentException e) {
            throw new PulbotException(e.getMessage());
        } catch (IOException e) {
            throw new PulbotException("Error reading file: " + e.getMessage());
        }
        return tasks;
    }

    /** Saves all tasks to disk in the application file format. */
    public void save(TaskList tasks) throws PulbotException {
        assert tasks != null : "Storage can only save an initialized task list";
        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            StringBuilder contents = new StringBuilder();
            for (Task task : tasks) {
                String taskType = task.getType() == TaskType.TODO ? "T"
                        : task.getType() == TaskType.DEADLINE ? "D" : "E";
                contents.append(taskType)
                        .append('\t')
                        .append(task.isDone() ? "1" : "0")
                        .append('\t')
                        .append(task.getDescription());
                if (task instanceof Deadline) {
                    contents.append('\t').append(formatDateTime(((Deadline) task).getBy()));
                } else if (task instanceof Event) {
                    contents.append('\t').append(formatDateTime(((Event) task).getFrom()))
                            .append('\t').append(formatDateTime(((Event) task).getTo()));
                }
                contents.append(System.lineSeparator());
            }
            Files.writeString(filePath, contents.toString());
        } catch (IOException e) {
            throw new PulbotException("Error writing to file: " + e.getMessage());
        }
    }

    private LocalDateTime parseStoredDateTime(String value) throws IllegalArgumentException {
        try {
            return LocalDateTime.parse(value.trim(), STORED_DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date in task file: " + value);
        }
    }

    private String formatDateTime(LocalDateTime value) {
        return value.format(STORED_DATE_FORMATTER);
    }

    /** Converts one validated storage line into a task. */
    private Task parseTask(String line) throws PulbotException {
        String[] columns = line.split("\t", -1);
        String type = columns[0];
        validateColumns(line, columns, type);

        Task task = createTask(columns, type);
        if (columns[1].equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /** Checks that a storage line contains valid common and type-specific fields. */
    private void validateColumns(String line, String[] columns, String type) throws PulbotException {
        if (!type.equals("T") && !type.equals("D") && !type.equals("E")) {
            throw new PulbotException("Invalid task type in file: " + type);
        }
        int expectedColumns = type.equals("T") ? 3 : type.equals("D") ? 4 : 5;
        if (columns.length != expectedColumns) {
            throw new PulbotException("Invalid line format in file: " + line);
        }
        String isMarked = columns[1];
        if (!isMarked.equals("0") && !isMarked.equals("1")) {
            throw new PulbotException("Invalid mark status in file: " + isMarked);
        }
        if (columns[2].isBlank()) {
            throw new PulbotException("Task description cannot be empty.");
        }
        if (type.equals("D") && columns[3].isBlank()) {
            throw new PulbotException("Deadline date cannot be empty.");
        }
        if (type.equals("E") && (columns[3].isBlank() || columns[4].isBlank())) {
            throw new PulbotException("Event start and end times cannot be empty.");
        }
    }

    /** Creates the task represented by validated storage columns. */
    private Task createTask(String[] columns, String type) throws PulbotException {
        String description = columns[2];
        switch (type) {
            case "T":
                return new Todo(description);
            case "D":
                return new Deadline(description, parseStoredDateTime(columns[3]));
            case "E":
                return new Event(description,
                        parseStoredDateTime(columns[3]), parseStoredDateTime(columns[4]));
            default:
                throw new PulbotException("Invalid task type in file: " + type);
        }
    }
}
