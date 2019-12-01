package pl.connectis.todo;

import fi.iki.elonen.NanoHTTPD;
import pl.connectis.todo.controller.TaskController;

import static fi.iki.elonen.NanoHTTPD.Method.GET;
import static fi.iki.elonen.NanoHTTPD.Method.POST;
import static fi.iki.elonen.NanoHTTPD.Response.Status.NOT_FOUND;

public class RequestUrlMapper {

    private final TaskController taskController = new TaskController();

    public NanoHTTPD.Response delegateRequest(NanoHTTPD.IHTTPSession session) {
        String uri = session.getUri();
        if (session.getMethod().equals(GET) && "/todos/getAll".equals(uri)) {
            return taskController.handleGetAll(session);
        } else if (session.getMethod().equals(GET) && "/todos/getSingle".equals(uri)) {
            return taskController.handleGetSingle(session);
        } else if (session.getMethod().equals(POST) && "/todos".equals(uri)) {
            return taskController.handleAdd(session);
        } else {
            return NanoHTTPD.newFixedLengthResponse(NOT_FOUND, "text/plain", "Not found");
        }
    }
}
