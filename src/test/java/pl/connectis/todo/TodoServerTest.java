package pl.connectis.todo;

import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.connectis.todo.domain.Task;
import pl.connectis.todo.repository.InMemoryTaskRepository;

import java.io.IOException;

import static io.restassured.RestAssured.with;
import static org.hamcrest.Matchers.*;

class TodoServerTest {

    private static final String TASK_ONE_JSON = "{\n" +
            "    \"name\": \"First task\",\n" +
            "    \"description\": \"Do something\",\n" +
            "    \"priority\": 1,\n" +
            "    \"assigned\": \"Arnold\"\n" +
            "}";

    private static final String TASK_TWO_JSON = "{\n" +
            "    \"name\": \"Second task\",\n" +
            "    \"description\": \"Do something else\",\n" +
            "    \"priority\": 2,\n" +
            "    \"assigned\": \"John\"\n" +
            "}";

    private static final String INVALID_JSON = "{\n" +
            "    \"name\": \"First task\",\n" +
            "    \"description\": \"Do something\",\n" +
            "    \"priority\":" +
            "    \"assigned\": \"Arnold\"\n" +
            "}";

    private static final String INVALID_TASK_JSON = "{\n" +
            "    \"description\": \"Do something\",\n" +
            "    \"priority\": 1,\n" +
            "    \"assigned\": \"Arnold\"\n" +
            "}";

    private static final int SERVER_PORT = 8090;

    private TodoServer bookshelfApp;

    @BeforeAll
    static void beforeAll() {
        RestAssured.port = SERVER_PORT;
    }

    @BeforeEach
    void beforeEach() throws IOException {
        bookshelfApp = new TodoServer(SERVER_PORT, new InMemoryTaskRepository());
        bookshelfApp.start(5000, false);
    }

    @AfterEach
    void afterEach() {
        bookshelfApp.stop();
    }

    @Test
    void addTaskShouldReturnSuccess() {
        with()
                .body(TASK_ONE_JSON)
                .when()
                .post("/todos")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("name", equalTo("First task"))
                .body("description", equalTo("Do something"))
                .body("priority", equalTo(1))
                .body("assigned", equalTo("Arnold"));
    }

    @Test
    void addWithInvalidJsonShouldReturnBadRequest() {
        with()
                .body(INVALID_JSON)
                .when()
                .post("/todos")
                .then()
                .statusCode(400)
                .body(equalTo("Invalid body"));
    }

    @Test
    void addTaskWithoutNameShouldReturnBadRequest() {
        with()
                .body(INVALID_TASK_JSON)
                .when()
                .post("/todos")
                .then()
                .statusCode(400)
                .body(equalTo("Task `name` is required"));
    }

    @Test
    void getAllShouldReturnEmptyList() {
        with()
                .get("/todos")
                .then()
                .statusCode(200)
                .body("", hasSize(0));
    }

    @Test
    void getAllShouldReturnTaskFromRepository() {
        long firstTaskId = addTaskAndGetId(TASK_ONE_JSON);

        with()
                .get("/todos")
                .then()
                .statusCode(200)
                .body("", hasSize(1))
                .body("id", hasItem(firstTaskId))
                .body("name", hasItem("First task"))
                .body("description", hasItem("Do something"))
                .body("priority", hasItem(1))
                .body("assigned", hasItem("Arnold"))
                .body("completed", hasItem(false));
    }

    @Test
    void getAllShouldReturnTwoTasksFromRepository() {
        long firstTaskId = addTaskAndGetId(TASK_ONE_JSON);
        long secondTaskId = addTaskAndGetId(TASK_TWO_JSON);

        with()
                .get("/todos")
                .then()
                .statusCode(200)
                .body("", hasSize(2))
                .body("id", hasItems(firstTaskId, secondTaskId))
                .body("name", hasItems("First task", "Second task"))
                .body("description", hasItems("Do something", "Do something else"))
                .body("priority", hasItems(1, 2))
                .body("assigned", hasItems("Arnold", "John"))
                .body("completed", hasItems(false, false));
    }

    @Test
    void getSingleValidatesIdParameterIsPassed() {
        with()
                .get("/todos/getSingle")
                .then()
                .statusCode(400)
                .body(equalTo("No `id` parameter"));
    }

    @Test
    void getSingleValidatesNoMoreThanOneIdParameterIsPassed() {
        with()
                .param("id", 1)
                .param("id", 2)
                .get("/todos/getSingle")
                .then()
                .statusCode(400)
                .body(equalTo("More than one `id` parameter"));
    }

    @Test
    void getSingleShouldReturnNotFound() {
        with()
                .param("id", 123)
                .get("/todos/getSingle")
                .then()
                .statusCode(404)
                .body(equalTo("Task with given id doesn't exist"));
    }

    @Test
    void getSingleShouldReturnTask() {
        long firstTaskId = addTaskAndGetId(TASK_ONE_JSON);

        with()
                .param("id", firstTaskId)
                .get("/todos/getSingle")
                .then()
                .statusCode(200)
                .body("id", equalTo(firstTaskId))
                .body("name", equalTo("First task"))
                .body("description", equalTo("Do something"))
                .body("priority", equalTo(1))
                .body("assigned", equalTo("Arnold"))
                .body("completed", equalTo(false));
    }

    private long addTaskAndGetId(String jsonBody) {
        return with()
                .body(jsonBody)
                .post("/todos")
                .then()
                .statusCode(201)
                .extract()
                .as(Task.class)
                .getId();
    }
}