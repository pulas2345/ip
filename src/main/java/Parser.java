public class Parser {
    public enum Type {
        BYE, MARK, UNMARK, DELETE, LIST, ON, TODO, DEADLINE, EVENT
    }

    public record Command(Type type, String firstArgument, String secondArgument, String thirdArgument) {
    }

    public Command parse(String input) throws PulbotException {
        if (input.equals("bye")) {
            return command(Type.BYE);
        }
        if (input.equals("list")) {
            return command(Type.LIST);
        }
        if (startsWithCommand(input, "mark")) {
            return command(Type.MARK, input.substring(4).trim());
        }
        if (startsWithCommand(input, "unmark")) {
            return command(Type.UNMARK, input.substring(6).trim());
        }
        if (startsWithCommand(input, "delete")) {
            return command(Type.DELETE, input.substring(6).trim());
        }
        if (startsWithCommand(input, "on")) {
            return command(Type.ON, input.substring(2).trim());
        }
        if (startsWithCommand(input, "todo")) {
            String description = input.substring(4).trim();
            if (description.isEmpty()) {
                throw new PulbotException("Please include a description.");
            }
            return command(Type.TODO, description);
        }
        if (startsWithCommand(input, "deadline")) {
            String details = input.substring(8).trim();
            int byIndex = details.indexOf(" /by ");
            if (byIndex <= 0 || byIndex + 5 >= details.length()) {
                throw new PulbotException("Please use: deadline <description> /by <when>.");
            }
            return command(Type.DEADLINE, details.substring(0, byIndex).trim(),
                    details.substring(byIndex + 5).trim());
        }
        if (startsWithCommand(input, "event")) {
            String details = input.substring(5).trim();
            int fromIndex = details.indexOf(" /from ");
            int toIndex = details.indexOf(" /to ", fromIndex + 7);
            if (fromIndex <= 0 || toIndex <= fromIndex + 7 || toIndex + 5 >= details.length()) {
                throw new PulbotException("Please use: event <description> /from <start> /to <end>.");
            }
            return command(Type.EVENT, details.substring(0, fromIndex).trim(),
                    details.substring(fromIndex + 7, toIndex).trim(), details.substring(toIndex + 5).trim());
        }
        throw new PulbotException("Invalid command. Please read the instructions and try again.");
    }

    private boolean startsWithCommand(String input, String command) {
        return input.equals(command) || input.startsWith(command + " ");
    }

    private Command command(Type type) {
        return new Command(type, "", "", "");
    }

    private Command command(Type type, String firstArgument) {
        return new Command(type, firstArgument, "", "");
    }

    private Command command(Type type, String firstArgument, String secondArgument) {
        return new Command(type, firstArgument, secondArgument, "");
    }

    private Command command(Type type, String firstArgument, String secondArgument, String thirdArgument) {
        return new Command(type, firstArgument, secondArgument, thirdArgument);
    }

}
