import java.util.Scanner;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * Starts Pulbot and stores tasks entered by the user.
 */
public class Pulbot {
    private static final String INDENT = "    ";
    private static final String SEPARATOR = INDENT + "_".repeat(80);
    private static final Path DATA_FILE = Path.of("data", "pulbot.txt");

    /**
     * Runs Pulbot until the user enters "bye" command.
     */
    public static void main(String[] args) {
        String banner = """
                        ##### ##                ###         ##### ##
                     ######  /###                ###     ######  /##
                    /#   /  /  ###                ##    /#   /  / ##                  #
                   /    /  /    ###               ##   /    /  /  ##                 ##
                       /  /      ##               ##       /  /   /                  ##
                      ## ##      ## ##   ####     ##      ## ##  /        /###     ########
                      ## ##      ##  ##    ###  / ##      ## ## /        / ###  / ########
                    /### ##      /   ##     ###/  ##      ## ##/        /   ###/     ##
                   / ### ##     /    ##      ##   ##      ## ## ###    ##    ##      ##
                      ## ######/     ##      ##   ##      ## ##   ###  ##    ##      ##
                      ## ######      ##      ##   ##      #  ##     ## ##    ##      ##
                      ## ##          ##      ##   ##         /      ## ##    ##      ##
                      ## ##          ##      /#   ##     /##/     ###  ##    ##      ##
                      ## ##           ######/ ##  ### / /  ########     ######       ##
                 ##   ## ##            #####   ##  ##/ /     ####        ####         ##
                ###   #  /                             #
                 ###    /                               ##
                  #####/
                    ###
                """;
        System.out.println(banner);
        System.out.println(INDENT + " Hello! I'm PulBot.");
        System.out.println(INDENT + " Enter your tasks and I will add them to your list.");
        System.out.println(INDENT + "   Type 'todo <description>' to add a todo.");
        System.out.println(INDENT + "   Type 'deadline <description> /by <when>' to add a deadline (d/M/yyyy HHmm).");
        System.out.println(
                INDENT + "   Type 'event <description> /from <start> /to <end>' to add an event (d/M/yyyy HHmm).");
        System.out.println(INDENT + "   Type 'list' to view your list.");
        System.out.println(INDENT + "   Type 'on <date>' to view deadlines and events on a date (d/M/yyyy).");
        System.out.println(INDENT + "   Type 'mark <number>' to mark a task as done.");
        System.out.println(INDENT + "   Type 'unmark <number>' to unmark a task.");
        System.out.println(INDENT + "   Type 'delete <number>' to remove a task.");
        System.out.println(INDENT + "   Type 'bye' to exit.");
        System.out.println(SEPARATOR + "\n");

        Scanner scanner = new Scanner(System.in);
        TaskList tasks = new TaskList();
        try {
            readTasks(tasks);
        } catch (PulbotException e) {
            System.out.println(INDENT + " \u001B[31m \u26A0 ERROR \u26A0 \u001B[0m" + e.getMessage());
        }

        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            System.out.println(SEPARATOR);

            try {
                if (input.equals("bye")) {
                    System.out.println(INDENT + " So soon? Just say you hate me. Bye.");
                    System.out.println(SEPARATOR + "\n");
                    break;
                }

                if (input.equals("mark") || input.startsWith("mark ")) {
                    int index = getTaskIndex(input.substring(4).trim(), tasks.size());
                    tasks.get(index).markAsDone();
                    System.out.println(INDENT + " I've marked this task as done:");
                    System.out.println(INDENT + "   " + tasks.get(index));
                    saveTasks(tasks);
                } else if (input.equals("unmark") || input.startsWith("unmark ")) {
                    int index = getTaskIndex(input.substring(6).trim(), tasks.size());
                    tasks.get(index).markAsNotDone();
                    System.out.println(INDENT + " I've unmarked this task:");
                    System.out.println(INDENT + "   " + tasks.get(index));
                    saveTasks(tasks);
                } else if (input.equals("delete") || input.startsWith("delete ")) {
                    int index = getTaskIndex(input.substring(6).trim(), tasks.size());
                    Task removedTask = tasks.remove(index);
                    System.out.println(INDENT + " I have deleted this task:");
                    System.out.println(INDENT + "   \u001B[31m" + removedTask + "\u001B[0m");
                    printTaskCount(tasks.size());
                    saveTasks(tasks);
                } else if (input.equals("list")) {
                    if (tasks.isEmpty()) {
                        System.out.println(INDENT + " Your list is empty.");
                    } else {
                        System.out.println(INDENT + " Here are the tasks in your list:");
                        for (int i = 0; i < tasks.size(); i++) {
                            System.out.println(INDENT + " " + (i + 1) + "." + tasks.get(i));
                        }
                    }
                } else if (input.equals("on") || input.startsWith("on ")) {
                    LocalDate date = parseDate(input.substring(2).trim());
                    printTasksOnDate(tasks, date);
                } else if (input.equals("todo") || input.startsWith("todo ")) {
                    String description = input.substring(4).trim();
                    if (description.isEmpty()) {
                        throw new PulbotException("Please include a description.");
                    }
                    Task task = new Todo(description);
                    tasks.add(task);
                    printAddedTask(task, tasks.size());
                    saveTasks(tasks);
                } else if (input.equals("deadline") || input.startsWith("deadline ")) {
                    String details = input.substring(8).trim();
                    int byIndex = details.indexOf(" /by ");
                    if (byIndex <= 0 || byIndex + 5 >= details.length()) {
                        throw new PulbotException("Please use: deadline <description> /by <when>.");
                    }
                    String description = details.substring(0, byIndex).trim();
                    String by = details.substring(byIndex + 5).trim();
                    Task task = new Deadline(description, parseDateTime(by));
                    tasks.add(task);
                    printAddedTask(task, tasks.size());
                    saveTasks(tasks);
                } else if (input.equals("event") || input.startsWith("event ")) {
                    String details = input.substring(5).trim();
                    int fromIndex = details.indexOf(" /from ");
                    int toIndex = details.indexOf(" /to ", fromIndex + 7);
                    if (fromIndex <= 0 || toIndex <= fromIndex + 7 || toIndex + 5 >= details.length()) {
                        throw new PulbotException("Please use: event <description> /from <start> /to <end>.");
                    }
                    String description = details.substring(0, fromIndex).trim();
                    String from = details.substring(fromIndex + 7, toIndex).trim();
                    String to = details.substring(toIndex + 5).trim();
                    Task task = new Event(description, parseDateTime(from), parseDateTime(to));
                    tasks.add(task);
                    printAddedTask(task, tasks.size());
                    saveTasks(tasks);
                } else {
                    throw new PulbotException("Invalid command. Please read the instructions and try again.");
                }
            } catch (PulbotException e) {
                System.out.println(INDENT + " \u001B[31m \u26A0 ERROR \u26A0 \u001B[0m" + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println(INDENT + " \u001B[31m \u26A0 ERROR \u26A0 \u001B[0m" + e.getMessage());
            }

            System.out.println(SEPARATOR + "\n");
        }
        scanner.close();
    }

    private static void printAddedTask(Task task, int taskCount) {
        System.out.println(INDENT + " I have added this task:");
        System.out.println(INDENT + "   " + task);
        printTaskCount(taskCount);
    }

    private static void printTaskCount(int taskCount) {
        String taskWord = taskCount == 1 ? "task" : "tasks";
        System.out.println(INDENT + " Now you have " + taskCount + " " + taskWord + " in the list.");
    }

    private static int getTaskIndex(String number, int taskCount) throws PulbotException {
        try {
            int index = Integer.parseInt(number) - 1;
            if (index < 0 || index >= taskCount) {
                throw new PulbotException("There is no task with that number. Please enter a valid task number.");
            }
            return index;
        } catch (NumberFormatException e) {
            throw new PulbotException("Please enter a valid task number.");
        }
    }

    /** Reads tasks from a file and adds them to the list */
    private static void readTasks(TaskList tasks) throws PulbotException {
        if (!Files.exists(DATA_FILE)) {
            return; // No file is an empty list
        }
        try (BufferedReader reader = Files.newBufferedReader(DATA_FILE)) {
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
                        task = new Event(description, parseStoredDateTime(columns[3]), parseStoredDateTime(columns[4]));
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
    }

    /** Updates the task file with the current list of tasks */
    private static void saveTasks(TaskList tasks) throws PulbotException {
        try {
            Path parent = DATA_FILE.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            StringBuilder contents = new StringBuilder();
            for (Task task : tasks) {
                contents.append(task.type == TaskType.TODO ? "T" : task.type == TaskType.DEADLINE ? "D" : "E")
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
            Files.writeString(DATA_FILE, contents.toString());
        } catch (IOException e) {
            throw new PulbotException("Error writing to file: " + e.getMessage());
        }
    }

    public static LocalDateTime parseDateTime(String value) throws IllegalArgumentException {
        try {
            return LocalDateTime.parse(value.trim(), DateTimeFormatter.ofPattern("d/M/uuuu HHmm"));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Use d/M/yyyy HHmm, for example 2/12/2019 1800.");
        }
    }

    private static LocalDateTime parseStoredDateTime(String value) throws IllegalArgumentException {
        try {
            return LocalDateTime.parse(value.trim(),
                    DateTimeFormatter.ofPattern("MMM dd uuuu h:mm a", Locale.ENGLISH));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date in task file: " + value);
        }
    }

    public static String formatDateTime(LocalDateTime value) {
        return value.format(DateTimeFormatter.ofPattern("MMM dd uuuu h:mm a", Locale.ENGLISH));
    }

    private static LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value, DateTimeFormatter.ofPattern("d/M/uuuu"));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Use d/M/yyyy for the date, for example 2/12/2019.");
        }
    }

    private static void printTasksOnDate(TaskList tasks, LocalDate date) {
        boolean found = false;
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            boolean occursOnDate = task instanceof Deadline
                    && ((Deadline) task).getBy().toLocalDate().equals(date)
                    || task instanceof Event
                            && !date.isBefore(((Event) task).getFrom().toLocalDate())
                            && !date.isAfter(((Event) task).getTo().toLocalDate());
            if (occursOnDate) {
                if (!found) {
                    System.out.println(INDENT + " Tasks on "
                            + date.format(DateTimeFormatter.ofPattern("MMM dd uuuu", Locale.ENGLISH)) + ":");
                }
                System.out.println(INDENT + " " + (i + 1) + "." + task);
                found = true;
            }
        }
        if (!found) {
            System.out.println(INDENT + " No deadlines or events on "
                    + date.format(DateTimeFormatter.ofPattern("MMM dd uuuu", Locale.ENGLISH)) + ".");
        }
    }
}
