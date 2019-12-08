package pl.connectis.todo.domain;

public class Task {
    private Long id;
    private String name;
    private String description;
    private String assigned;
    private int priority;
    private boolean completed;

    public Task() {
    }

    public Task(Long id, String name, String description, String assigned, int priority, boolean completed) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.assigned = assigned;
        this.priority = priority;
        this.completed = completed;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAssigned() {
        return assigned;
    }

    public void setAssigned(String assigned) {
        this.assigned = assigned;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
