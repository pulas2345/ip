package pulbot.ui;

import pulbot.task.Deadline;
import pulbot.task.Event;
import pulbot.task.Task;
import pulbot.task.TaskList;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Scanner;

/** Handles Pulbot's console input and output. */
public class Ui {
    private static final String INDENT = "    ";
    private static final String SEPARATOR = INDENT + "_".repeat(80);
    private final Scanner scanner;

    /** Creates a console user interface backed by standard input. */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /** Displays Pulbot's startup instructions. */
    public void showWelcome() {
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
        System.out.println(INDENT + "   Type 'event <description> /from <start> /to <end>' to add an event (d/M/yyyy HHmm).");
        System.out.println(INDENT + "   Type 'list' to view your list.");
        System.out.println(INDENT + "   Type 'on <date>' to view deadlines and events on a date (d/M/yyyy).");
        System.out.println(INDENT + "   Type 'mark <number>' to mark a task as done.");
        System.out.println(INDENT + "   Type 'unmark <number>' to unmark a task.");
        System.out.println(INDENT + "   Type 'delete <number>' to remove a task.");
        System.out.println(INDENT + "   Type 'bye' to exit.");
        showSeparatorWithNewLine();
    }

    /** Returns whether another input line is available. */
    public boolean hasNextLine() {
        return scanner.hasNextLine();
    }

    /** Reads and returns the next input line. */
    public String nextLine() {
        return scanner.nextLine();
    }

    /** Displays a separator between interaction turns. */
    public void showSeparator() {
        System.out.println(SEPARATOR);
    }

    /** Displays the exit message. */
    public void showBye() {
        System.out.println(INDENT + " So soon? Just say you hate me. Bye.");
    }

    /** Displays an error message. */
    public void showError(String message) {
        System.out.println(INDENT + " \u001B[31m \u26A0 ERROR \u26A0 \u001B[0m" + message);
    }

    /** Displays confirmation for an added task. */
    public void showAddedTask(Task task, int taskCount) {
        System.out.println(INDENT + " I have added this task:");
        System.out.println(INDENT + "   " + task);
        showTaskCount(taskCount);
    }

    /** Displays the current number of tasks. */
    public void showTaskCount(int taskCount) {
        String taskWord = taskCount == 1 ? "task" : "tasks";
        System.out.println(INDENT + " Now you have " + taskCount + " " + taskWord + " in the list.");
    }

    /** Displays confirmation for a marked task. */
    public void showMarked(Task task) {
        System.out.println(INDENT + " I've marked this task as done:");
        System.out.println(INDENT + "   " + task);
    }

    /** Displays confirmation for an unmarked task. */
    public void showUnmarked(Task task) {
        System.out.println(INDENT + " I've unmarked this task:");
        System.out.println(INDENT + "   " + task);
    }

    /** Displays confirmation for a deleted task. */
    public void showDeleted(Task task, int taskCount) {
        System.out.println(INDENT + " I have deleted this task:");
        System.out.println(INDENT + "   \u001B[31m" + task + "\u001B[0m");
        showTaskCount(taskCount);
    }

    /** Displays every task in the supplied list. */
    public void showList(TaskList tasks) {
        if (tasks.isEmpty()) {
            System.out.println(INDENT + " Your list is empty.");
            return;
        }
        System.out.println(INDENT + " Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println(INDENT + " " + (i + 1) + "." + tasks.get(i));
        }
    }

    /** Displays deadlines and events occurring on the supplied date. */
    public void showTasksOnDate(TaskList tasks, LocalDate date) {
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
                    System.out.println(INDENT + " Tasks on " + formatDate(date) + ":");
                }
                System.out.println(INDENT + " " + (i + 1) + "." + task);
                found = true;
            }
        }
        if (!found) {
            System.out.println(INDENT + " No deadlines or events on " + formatDate(date) + ".");
        }
    }

    /** Closes the console input scanner. */
    public void close() {
        scanner.close();
    }

    private void showSeparatorWithNewLine() {
        System.out.println(SEPARATOR + "\n");
    }

    private String formatDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("MMM dd uuuu", Locale.ENGLISH));
    }
}
