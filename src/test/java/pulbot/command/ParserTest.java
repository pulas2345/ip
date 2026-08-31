package pulbot.command;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import pulbot.PulbotException;
import pulbot.storage.Storage;
import pulbot.task.Deadline;
import pulbot.task.Event;
import pulbot.task.TaskList;
import pulbot.task.Todo;
import pulbot.ui.Ui;

/** Tests conversion of user input into commands. */
public class ParserTest {
    private final Parser parser = new Parser();

    @Test
    public void parse_validCommands_returnsMatchingCommandTypes() throws PulbotException {
        assertInstanceOf(ExitCommand.class, parser.parse("bye"));
        assertInstanceOf(ListCommand.class, parser.parse("list"));
        assertInstanceOf(MarkCommand.class, parser.parse("mark 1"));
        assertInstanceOf(UnmarkCommand.class, parser.parse("unmark 1"));
        assertInstanceOf(DeleteCommand.class, parser.parse("delete 1"));
        assertInstanceOf(FindCommand.class, parser.parse("find book"));
        assertInstanceOf(OnCommand.class, parser.parse("on 2/12/2019"));
        assertInstanceOf(AddCommand.class, parser.parse("todo read book"));
        assertInstanceOf(AddCommand.class,
                parser.parse("deadline return book /by 2/12/2019 1800"));
        assertInstanceOf(AddCommand.class,
                parser.parse("event lecture /from 2/12/2019 1400 /to 2/12/2019 1600"));
    }

    @Test
    public void parse_missingRequiredDetails_throwsException() {
        assertThrows(PulbotException.class, () -> parser.parse("todo"));
        assertThrows(PulbotException.class, () -> parser.parse("find"));
        assertThrows(PulbotException.class, () -> parser.parse("deadline return book"));
        assertThrows(PulbotException.class, () -> parser.parse("event lecture"));
    }

    @Test
    public void parse_unknownCommand_throwsException() {
        assertThrows(PulbotException.class, () -> parser.parse("unknown command"));
        assertThrows(PulbotException.class, () -> parser.parse("listing"));
    }

    @Test
    public void parse_invalidDates_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> parser.parse("on 2019-12-02"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("on 31/2/2019"));
        assertThrows(IllegalArgumentException.class,
                () -> parser.parse("deadline return book /by 31/2/2019 1800"));
        assertThrows(IllegalArgumentException.class,
                () -> parser.parse("event lecture /from tomorrow /to 2/12/2019 1600"));
    }

    @Test
    public void parse_addCommands_preservesTaskDetails() throws PulbotException {
        TaskList tasks = new TaskList();
        Ui ui = new SilentUi();
        Storage storage = new NoOpStorage();

        parser.parse("todo read book").execute(tasks, ui, storage);
        parser.parse("deadline return book /by 2/12/2019 1800").execute(tasks, ui, storage);
        parser.parse("event lecture /from 3/12/2019 1400 /to 3/12/2019 1600")
                .execute(tasks, ui, storage);

        Todo todo = assertInstanceOf(Todo.class, tasks.get(0));
        assertEquals("read book", todo.getDescription());
        Deadline deadline = assertInstanceOf(Deadline.class, tasks.get(1));
        assertEquals("return book", deadline.getDescription());
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), deadline.getBy());
        Event event = assertInstanceOf(Event.class, tasks.get(2));
        assertEquals("lecture", event.getDescription());
        assertEquals(LocalDateTime.of(2019, 12, 3, 14, 0), event.getFrom());
        assertEquals(LocalDateTime.of(2019, 12, 3, 16, 0), event.getTo());
    }

    /** Suppresses console output while parsed add commands are executed. */
    private static class SilentUi extends Ui {
        @Override
        public void showAddedTask(pulbot.task.Task task, int taskCount) {
            // No output is needed for this parser test.
        }
    }

    /** Avoids disk writes while parsed add commands are executed. */
    private static class NoOpStorage extends Storage {
        NoOpStorage() {
            super("unused");
        }

        @Override
        public void save(TaskList tasks) {
            // Persistence is tested separately in StorageTest.
        }
    }
}
