import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * Starts Pulbot and stores tasks entered by the user.
 */
public class Pulbot {
    /**
     * Runs Pulbot until the user enters "bye" command.
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        Parser parser = new Parser();
        Storage storage = new Storage("data/pulbot.txt");
        ui.showWelcome();
        TaskList tasks = new TaskList();
        try {
            tasks = storage.load();
        } catch (PulbotException e) {
            ui.showError(e.getMessage());
        }

        while (ui.hasNextLine()) {
            String input = ui.nextLine();
            ui.showSeparator();

            try {
                Parser.Command command = parser.parse(input);
                switch (command.type()) {
                case BYE:
                    ui.showBye();
                    break;
                case MARK:
                    int index = getTaskIndex(command.firstArgument(), tasks.size());
                    tasks.get(index).markAsDone();
                    ui.showMarked(tasks.get(index));
                    storage.save(tasks);
                    break;
                case UNMARK:
                    index = getTaskIndex(command.firstArgument(), tasks.size());
                    tasks.get(index).markAsNotDone();
                    ui.showUnmarked(tasks.get(index));
                    storage.save(tasks);
                    break;
                case DELETE:
                    index = getTaskIndex(command.firstArgument(), tasks.size());
                    Task removedTask = tasks.remove(index);
                    ui.showDeleted(removedTask, tasks.size());
                    storage.save(tasks);
                    break;
                case LIST:
                    ui.showList(tasks);
                    break;
                case ON:
                    LocalDate date = parseDate(command.firstArgument());
                    ui.showTasksOnDate(tasks, date);
                    break;
                case TODO:
                    Task task = new Todo(command.firstArgument());
                    tasks.add(task);
                    ui.showAddedTask(task, tasks.size());
                    storage.save(tasks);
                    break;
                case DEADLINE:
                    task = new Deadline(command.firstArgument(), parseDateTime(command.secondArgument()));
                    tasks.add(task);
                    ui.showAddedTask(task, tasks.size());
                    storage.save(tasks);
                    break;
                case EVENT:
                    task = new Event(command.firstArgument(), parseDateTime(command.secondArgument()),
                            parseDateTime(command.thirdArgument()));
                    tasks.add(task);
                    ui.showAddedTask(task, tasks.size());
                    storage.save(tasks);
                    break;
                default:
                    throw new PulbotException("Invalid command. Please read the instructions and try again.");
                }
            } catch (PulbotException e) {
                ui.showError(e.getMessage());
            } catch (IllegalArgumentException e) {
                ui.showError(e.getMessage());
            }

                ui.showSeparator();
            System.out.println();
        }
        ui.close();
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

    public static LocalDateTime parseDateTime(String value) throws IllegalArgumentException {
        try {
            return LocalDateTime.parse(value.trim(), DateTimeFormatter.ofPattern("d/M/uuuu HHmm"));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Use d/M/yyyy HHmm, for example 2/12/2019 1800.");
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

}
