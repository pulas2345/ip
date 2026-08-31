package pulbot.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests the ordered task collection operations. */
public class TaskListTest {
    @Test
    public void collectionOperations_addGetIterateAndRemove_inOrder() {
        Task first = new Todo("first");
        Task second = new Todo("second");
        List<Task> source = new ArrayList<>(List.of(first));
        TaskList tasks = new TaskList(source);
        source.clear();

        assertFalse(tasks.isEmpty());
        assertEquals(1, tasks.size());
        assertSame(first, tasks.get(0));

        tasks.add(second);
        Iterator<Task> iterator = tasks.iterator();
        assertSame(first, iterator.next());
        assertSame(second, iterator.next());
        assertFalse(iterator.hasNext());

        assertSame(first, tasks.remove(0));
        assertSame(second, tasks.remove(0));
        assertTrue(tasks.isEmpty());
    }
}
