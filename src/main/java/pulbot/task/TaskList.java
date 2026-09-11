package pulbot.task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/** Represents the ordered collection of tasks managed by Pulbot. */
public class TaskList implements Iterable<Task> {
    private final List<Task> tasks;

    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /** Creates an independent task list containing the supplied tasks in order. */
    public TaskList(Collection<Task> tasks) {
        assert tasks != null : "A task list cannot be created from a null collection";
        this.tasks = new ArrayList<>(tasks);
    }

    /** Adds a validated task to the end of the list. */
    public void add(Task task) {
        assert task != null : "A task list cannot contain null tasks";
        tasks.add(task);
    }

    public Task get(int index) {
        return tasks.get(index);
    }

    public Task remove(int index) {
        return tasks.remove(index);
    }

    public int size() {
        return tasks.size();
    }

    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    @Override
    public Iterator<Task> iterator() {
        return tasks.iterator();
    }
}
