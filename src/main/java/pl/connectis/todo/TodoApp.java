package pl.connectis.todo;

import pl.connectis.todo.repository.DatabaseTaskRepository;

import java.io.IOException;

public class TodoApp {

    public static void main(String[] args) {
        try {
            new TodoServer(8080, new DatabaseTaskRepository())
                    .start(5000, false);
        } catch (IOException ex) {
            System.err.println("Server was unable to start: " + ex.getMessage());
        }
    }
}
