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

    public TaskList(Collection<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    public void add(Task task) {
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
