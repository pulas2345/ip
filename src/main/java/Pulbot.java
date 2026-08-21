import java.util.Scanner;

/**
 * Starts Pulbot and echos commands entered by the user.
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
        System.out.println(INDENT + " What can I do for you?");
        System.out.println(SEPARATOR + "\n");

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            System.out.println(SEPARATOR);

            System.out.println(INDENT + " " + input);
            System.out.println(SEPARATOR + "\n");

            if (input.equals("bye")) {
                System.out.println(INDENT + " Bye. Hope to see you again soon!");
                System.out.println(SEPARATOR + "\n");
            }
        }
        scanner.close();
    }
}
