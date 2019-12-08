package pl.connectis.todo;

import fi.iki.elonen.NanoHTTPD;
import pl.connectis.todo.controller.TaskController;
import pl.connectis.todo.repository.TaskRepository;
import pl.connectis.todo.util.HttpConstants;

import static fi.iki.elonen.NanoHTTPD.Method.GET;
import static fi.iki.elonen.NanoHTTPD.Method.POST;
import static fi.iki.elonen.NanoHTTPD.Response.Status.NOT_FOUND;

public class RequestUrlMapper {

    private final TaskController taskController;

    public RequestUrlMapper(TaskRepository taskRepository) {
        taskController = new TaskController(taskRepository);
    }

    public NanoHTTPD.Response delegateRequest(NanoHTTPD.IHTTPSession session) {
        String uri = session.getUri();
        if (session.getMethod().equals(GET) && "/todos".equals(uri)) {
            return taskController.handleGetAll(session);

        } else if (session.getMethod().equals(GET) && "/todos/getSingle".equals(uri)) {
            return taskController.handleGetSingle(session);

        } else if (session.getMethod().equals(POST) && "/todos".equals(uri)) {
            return taskController.handleAdd(session);

        } else {
            return NanoHTTPD.newFixedLengthResponse(NOT_FOUND, HttpConstants.MIME_TYPE_TEXT_PLAIN, "Not found");
        }
    }
}
