package pulbot;

import pulbot.command.Command;
import pulbot.command.Parser;
import pulbot.storage.Storage;
import pulbot.task.TaskList;
import pulbot.ui.Ui;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

/**
 * Starts Pulbot and stores tasks entered by the user.
 */
public class Pulbot {
    private static final String DEFAULT_FILE_PATH = "data/pulbot.txt";

    private final Parser parser;
    private final Storage storage;
    private TaskList tasks;

    public Pulbot() {
        parser = new Parser();
        storage = new Storage(DEFAULT_FILE_PATH);
        try {
            tasks = storage.load();
        } catch (PulbotException e) {
            tasks = new TaskList();
        }
    }

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

    /** Generates a response for the user's chat message. */
    public String getResponse(String input) {
        ByteArrayOutputStream response = new ByteArrayOutputStream();
        try (PrintStream output = new PrintStream(response, true, StandardCharsets.UTF_8)) {
            Ui ui = new Ui(InputStream.nullInputStream(), output);
            try {
                Command command = parser.parse(input);
                command.execute(tasks, ui, storage);
            } catch (PulbotException | IllegalArgumentException e) {
                ui.showError(e.getMessage());
            } finally {
                ui.close();
            }
        }
        return response.toString(StandardCharsets.UTF_8)
                .replaceAll("\\u001B\\[[;\\d]*m", "")
                .strip();
    }

    /** Parses a date and time entered using Pulbot's command format. */
    public static LocalDateTime parseDateTime(String value) throws IllegalArgumentException {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/uuuu HHmm")
                    .withResolverStyle(ResolverStyle.STRICT);
            return LocalDateTime.parse(value.trim(), formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Use d/M/yyyy HHmm, for example 2/12/2019 1800.");
        }
    }

    /** Formats a date and time for display to the user. */
    public static String formatDateTime(LocalDateTime value) {
        return value.format(DateTimeFormatter.ofPattern("MMM dd uuuu h:mm a", Locale.ENGLISH));
    }

}
