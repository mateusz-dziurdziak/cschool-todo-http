package pl.connectis.todo.repository;

import pl.connectis.todo.domain.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryTaskRepository implements TaskRepository {

    private final List<Task> tasks = new ArrayList<>();

    @Override
    public List<Task> getAll() {
        return new ArrayList<>(tasks);
    }

    @Override
    public Task getById(long id) {
        for (Task task : tasks) {
            if (task.getId() == id) {
                return task;
            }
        }
        return null;
    }

    @Override
    public Task add(Task task) {
        task.setId(System.currentTimeMillis());
        tasks.add(task);
        return task;
    }
}
