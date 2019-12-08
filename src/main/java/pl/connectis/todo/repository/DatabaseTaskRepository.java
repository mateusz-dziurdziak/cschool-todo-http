package pl.connectis.todo.repository;

import pl.connectis.todo.domain.Task;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseTaskRepository implements TaskRepository {

    @Override
    public List<Task> getAll() {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = connectToDatabase();
            statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery("select * from task");

            List<Task> tasks = new ArrayList<>();
            while (resultSet.next()) {
                Task task = readTask(resultSet);
                tasks.add(task);
            }
            return tasks;
        } catch (SQLException e) {
            System.err.println("Exception occurred during database query: " + e.getMessage());
            throw new RuntimeException("Exception occured during database query");
        } finally {
            closeDatabaseResources(connection, statement);
        }
    }

    @Override
    public Task getById(long id) {
        String selectById = "select * from task where id = ?;";
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connectToDatabase();
            statement = connection.prepareStatement(selectById);
            statement.setLong(1, id);

            ResultSet resultSet = statement.executeQuery("select * from task");

            if (!resultSet.next()) {
                return null;
            }

            return readTask(resultSet);
        } catch (SQLException e) {
            System.err.println("Exception occurred during database insert: " + e.getMessage());
            throw new RuntimeException("Exception occured during database insert");
        } finally {
            closeDatabaseResources(connection, statement);
        }
    }

    @Override
    public Task add(Task task) {
        String insertTaskSql = "insert into task(name, description, priority, assigned, completed)"
                + " values (?, ?, ?, ?, ?)"
                + " returning *;";
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connectToDatabase();
            statement = connection.prepareStatement(insertTaskSql);

            statement.setString(1, task.getName());
            statement.setString(2, task.getDescription());
            statement.setInt(3, task.getPriority());
            statement.setString(4, task.getAssigned());
            statement.setBoolean(5, task.isCompleted());

            statement.execute();

            ResultSet resultSet = statement.getResultSet();
            if (!resultSet.next()) {
                throw new RuntimeException("Exception occurred during task insertion");
            }
            return readTask(resultSet);
        } catch (SQLException e) {
            System.err.println("Exception occurred during database insert: " + e.getMessage());
            throw new RuntimeException("Exception occured during database insert");
        } finally {
            closeDatabaseResources(connection, statement);
        }
    }

    private Connection connectToDatabase() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/todo",
                "todo_user",
                "todo_user");
    }

    private Task readTask(ResultSet resultSet) throws SQLException {
        long id = resultSet.getLong("id");
        String name = resultSet.getString("name");
        String description = resultSet.getString("description");
        String assigned = resultSet.getString("assigned");
        boolean completed = resultSet.getBoolean("completed");
        int priority = resultSet.getInt("priority");

        return new Task(id, name, description, assigned, priority, completed);
    }

    private void closeDatabaseResources(Connection connection, Statement statement) {
        try {
            if (statement != null) {
                statement.close();
            }

            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("System error during database resource closing: " + e.getMessage());
            throw new RuntimeException("System error during database resource closing: " + e.getMessage());
        }
    }
}
