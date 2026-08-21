import java.util.Scanner;

/**
 * Starts Pulbot and stores tasks entered by the user.
 */
public class Pulbot {
    private static final String INDENT = "    ";
    private static final String SEPARATOR = INDENT + "_".repeat(65);

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
        Task[] tasks = new Task[100];
        int taskCount = 0;

        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            System.out.println(SEPARATOR);

            if (input.equals("bye")) {
                System.out.println(INDENT + " Bye. Hope to see you again soon!");
                System.out.println(SEPARATOR + "\n");
                break;
            }

            if (input.startsWith("mark ")) {
                try {
                    int index = Integer.parseInt(input.substring(5)) - 1;
                    if (index >= 0 && index < taskCount) {
                        tasks[index].markAsDone();
                        System.out.println(INDENT + " I've marked this task as done:");
                        System.out.println(INDENT + "   " + tasks[index]);
                    } else {
                        System.out.println(INDENT + " Invalid task number.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println(INDENT + " Please provide a valid task number to mark.");
                }
            } else if (input.startsWith("unmark ")) {
                try {
                    int index = Integer.parseInt(input.substring(7)) - 1;
                    if (index >= 0 && index < taskCount) {
                        tasks[index].markAsNotDone();
                        System.out.println(INDENT + " I've unmarked this task:");
                        System.out.println(INDENT + "   " + tasks[index]);
                    } else {
                        System.out.println(INDENT + " Invalid task number.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println(INDENT + " Please provide a valid task number to unmark.");
                }
            } else if (input.equals("list")) {
                if (taskCount == 0) {
                    System.out.println(INDENT + " Your list is empty.");
                } else {
                    System.out.println(INDENT + " Here are the tasks in your list:");
                    for (int i = 0; i < taskCount; i++) {
                        System.out.println(INDENT + " " + (i + 1) + "." + tasks[i]);
                    }
                }
            } else if (input.startsWith("todo ")) {
                String description = input.substring(5).trim();
                if (description.isEmpty()) {
                    System.out.println(INDENT + " A todo needs a description.");
                } else {
                    tasks[taskCount] = new Todo(description);
                    taskCount++;
                    printAddedTask(tasks[taskCount - 1], taskCount);
                }
            } else if (input.startsWith("deadline ")) {
                String details = input.substring(9).trim();
                int byIndex = details.indexOf(" /by ");
                if (byIndex <= 0 || byIndex + 5 >= details.length()) {
                    System.out.println(INDENT + " Use: deadline <description> /by <when>");
                } else {
                    String description = details.substring(0, byIndex).trim();
                    String by = details.substring(byIndex + 5).trim();
                    tasks[taskCount] = new Deadline(description, by);
                    taskCount++;
                    printAddedTask(tasks[taskCount - 1], taskCount);
                }
            } else if (input.startsWith("event ")) {
                String details = input.substring(6).trim();
                int fromIndex = details.indexOf(" /from ");
                int toIndex = details.indexOf(" /to ", fromIndex + 7);
                if (fromIndex <= 0 || toIndex <= fromIndex + 7 || toIndex + 5 >= details.length()) {
                    System.out.println(INDENT + " Use: event <description> /from <start> /to <end>");
                } else {
                    String description = details.substring(0, fromIndex).trim();
                    String from = details.substring(fromIndex + 7, toIndex).trim();
                    String to = details.substring(toIndex + 5).trim();
                    tasks[taskCount] = new Event(description, from, to);
                    taskCount++;
                    printAddedTask(tasks[taskCount - 1], taskCount);
                }
            } else {
                System.out.println(INDENT + " Unknown command.");
            }

            System.out.println(SEPARATOR + "\n");
        }
        scanner.close();
    }

    private static void printAddedTask(Task task, int taskCount) {
        System.out.println(INDENT + " I have added this task:");
        System.out.println(INDENT + "   " + task);
        String taskWord = taskCount == 1 ? "task" : "tasks";
        System.out.println(INDENT + " Now you have " + taskCount + " " + taskWord + " in the list.");
    }
}
