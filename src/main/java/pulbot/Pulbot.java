package pulbot;

import pulbot.command.Command;
import pulbot.command.Parser;
import pulbot.storage.Storage;
import pulbot.task.TaskList;
import pulbot.ui.Ui;

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

        boolean isExit = false;
        while (ui.hasNextLine() && !isExit) {
            String input = ui.nextLine();
            ui.showSeparator();

            try {
                Command command = parser.parse(input);
                command.execute(tasks, ui, storage);
                isExit = command.isExit();
            } catch (PulbotException e) {
                ui.showError(e.getMessage());
            } catch (IllegalArgumentException e) {
                ui.showError(e.getMessage());
            } finally {
                ui.showSeparator();
                System.out.println();
            }
        }
        ui.close();
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

}
