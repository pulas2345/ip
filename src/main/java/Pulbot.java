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
                    for (int i = 0; i < taskCount; i++) {
                        System.out.println(INDENT + " " + (i + 1) + "." + tasks[i]);
                    }
                }
            } else {
                tasks[taskCount] = new Task(input);
                taskCount++;
                System.out.println(INDENT + " added " + input + " to your list.");
            }

            System.out.println(SEPARATOR + "\n");
        }
        scanner.close();
    }
}
