package pulbot.storage;

import pulbot.PulbotException;
import pulbot.task.Deadline;
import pulbot.task.Event;
import pulbot.task.Task;
import pulbot.task.TaskList;
import pulbot.task.TaskType;
import pulbot.task.Todo;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/** Handles loading tasks from and saving tasks to a file. */
public class Storage {
    private static final DateTimeFormatter STORED_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd uuuu h:mm a", Locale.ENGLISH);
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
                String[] columns = line.split("\t", -1);
                String type = columns[0];
                int expectedColumns = type.equals("T") ? 3 : type.equals("D") ? 4 : 5;
                if (!type.equals("T") && !type.equals("D") && !type.equals("E")) {
                    throw new PulbotException("Invalid task type in file: " + type);
                }
                if (columns.length != expectedColumns) {
                    throw new PulbotException("Invalid line format in file: " + line);
                }
                String isMarked = columns[1];
                if (!isMarked.equals("0") && !isMarked.equals("1")) {
                    throw new PulbotException("Invalid mark status in file: " + isMarked);
                }
                String description = columns[2];
                if (description.isBlank()) {
                    throw new PulbotException("Task description cannot be empty.");
                }
                if (type.equals("D") && columns[3].isBlank()) {
                    throw new PulbotException("Deadline date cannot be empty.");
                }
                if (type.equals("E") && (columns[3].isBlank() || columns[4].isBlank())) {
                    throw new PulbotException("Event start and end times cannot be empty.");
                }
                Task task;
                switch (type) {
                case "T":
                    task = new Todo(description);
                    break;
                case "D":
                    task = new Deadline(description, parseStoredDateTime(columns[3]));
                    break;
                case "E":
                    task = new Event(description,
                            parseStoredDateTime(columns[3]), parseStoredDateTime(columns[4]));
                    break;
                default:
                    throw new PulbotException("Invalid task type in file: " + type);
                }
                if (isMarked.equals("1")) {
                    task.markAsDone();
                }
                tasks.add(task);
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
}
