import java.util.ArrayList;
import java.util.Scanner;

/**
 * Starts Pulbot and stores tasks entered by the user.
 */
public class Pulbot {
    private static final String INDENT = "    ";
    private static final String SEPARATOR = INDENT + "_".repeat(80);

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
        System.out.println(INDENT + "   Type 'deadline <description> /by <when>' to add a deadline.");
        System.out.println(INDENT + "   Type 'event <description> /from <start> /to <end>' to add an event.");
        System.out.println(INDENT + "   Type 'list' to view your list.");
        System.out.println(INDENT + "   Type 'mark <number>' to mark a task as done.");
        System.out.println(INDENT + "   Type 'unmark <number>' to unmark a task.");
        System.out.println(INDENT + "   Type 'bye' to exit.");
        System.out.println(SEPARATOR + "\n");

        Scanner scanner = new Scanner(System.in);
        ArrayList<Task> tasks = new ArrayList<>();

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
                } else if (input.equals("unmark") || input.startsWith("unmark ")) {
                    int index = getTaskIndex(input.substring(6).trim(), tasks.size());
                    tasks.get(index).markAsNotDone();
                    System.out.println(INDENT + " I've unmarked this task:");
                    System.out.println(INDENT + "   " + tasks.get(index));
                } else if (input.equals("list")) {
                    if (tasks.isEmpty()) {
                        System.out.println(INDENT + " Your list is empty.");
                    } else {
                        System.out.println(INDENT + " Here are the tasks in your list:");
                        for (int i = 0; i < tasks.size(); i++) {
                            System.out.println(INDENT + " " + (i + 1) + "." + tasks.get(i));
                        }
                    }
                } else if (input.equals("todo") || input.startsWith("todo ")) {
                    String description = input.substring(4).trim();
                    if (description.isEmpty()) {
                        throw new PulbotException("Please include a description.");
                    }
                    Task task = new Todo(description);
                    tasks.add(task);
                    printAddedTask(task, tasks.size());
                } else if (input.equals("deadline") || input.startsWith("deadline ")) {
                    String details = input.substring(8).trim();
                    int byIndex = details.indexOf(" /by ");
                    if (byIndex <= 0 || byIndex + 5 >= details.length()) {
                        throw new PulbotException("Please use: deadline <description> /by <when>.");
                    }
                    String description = details.substring(0, byIndex).trim();
                    String by = details.substring(byIndex + 5).trim();
                    Task task = new Deadline(description, by);
                    tasks.add(task);
                    printAddedTask(task, tasks.size());
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
                    Task task = new Event(description, from, to);
                    tasks.add(task);
                    printAddedTask(task, tasks.size());
                } else {
                    throw new PulbotException("Invalid command. Please read the instructions and try again.");
                }
            } catch (PulbotException e) {
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
}
