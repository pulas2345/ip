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
        System.out.println(INDENT + " What tasks do you want to add to your list?");
        System.out.println(INDENT + " (Type 'list' to view your list, and 'bye' to exit.)");
        System.out.println(SEPARATOR + "\n");

        Scanner scanner = new Scanner(System.in);
        String[] tasks = new String[100];
        int taskCount = 0;

        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            System.out.println(SEPARATOR);

            if (input.equals("bye")) {
                System.out.println(INDENT + " Bye. Hope to see you again soon!");
                System.out.println(SEPARATOR + "\n");
                break;
            }

            if (input.equals("list")) {
                for (int i = 0; i < taskCount; i++) {
                    System.out.println(INDENT + " " + (i + 1) + ". " + tasks[i]);
                }              
            } else {
                tasks[taskCount] = input;
                taskCount++;
                System.out.println(INDENT + " added " + input + " to your list.");
            }

            System.out.println(SEPARATOR + "\n");
        }
        scanner.close();
    }
}
