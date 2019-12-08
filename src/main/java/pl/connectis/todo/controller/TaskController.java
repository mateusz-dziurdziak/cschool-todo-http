package pl.connectis.todo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.iki.elonen.NanoHTTPD;
import pl.connectis.todo.domain.Task;
import pl.connectis.todo.repository.TaskRepository;
import pl.connectis.todo.util.HttpConstants;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static fi.iki.elonen.NanoHTTPD.Response.Status.*;

public class TaskController {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final TaskRepository taskRepository;

    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public NanoHTTPD.Response handleGetAll(NanoHTTPD.IHTTPSession session) {
        List<Task> tasks = taskRepository.getAll();
        return asJsonResponse(OK, tasks);
    }

    public NanoHTTPD.Response handleGetSingle(NanoHTTPD.IHTTPSession session) {
        Map<String, List<String>> parameters = session.getParameters();
        List<String> ids = parameters.getOrDefault("id", Collections.emptyList());

        if (ids.isEmpty()) {
            return NanoHTTPD.newFixedLengthResponse(BAD_REQUEST, HttpConstants.MIME_TYPE_TEXT_PLAIN, "No `id` parameter");
        }

        if (ids.size() > 1) {
            return NanoHTTPD.newFixedLengthResponse(BAD_REQUEST, HttpConstants.MIME_TYPE_TEXT_PLAIN, "More than one `id` parameter");
        }

        String id = ids.get(0);

        long parsedId;
        try {
            parsedId = Long.parseLong(id);
        } catch (NumberFormatException nfe) {
            return NanoHTTPD.newFixedLengthResponse(BAD_REQUEST, HttpConstants.MIME_TYPE_TEXT_PLAIN, "Invalid `id` parameter");
        }

        Task task = taskRepository.getById(parsedId);

        if (task == null) {
            return NanoHTTPD.newFixedLengthResponse(NOT_FOUND, HttpConstants.MIME_TYPE_TEXT_PLAIN, "Task with given id doesn't exist");
        }

        return asJsonResponse(OK, task);
    }

    public NanoHTTPD.Response handleAdd(NanoHTTPD.IHTTPSession session) {
        String contentLength = session.getHeaders().get(HttpConstants.HEADER_CONTENT_LENGTH);
        int contentLengthInt = Integer.parseInt(contentLength);

        byte[] buffer = new byte[contentLengthInt];

        Task task;
        try {
            session.getInputStream().read(buffer, 0, contentLengthInt);
            task = objectMapper.readValue(buffer, Task.class);
        } catch (IOException e) {
            System.err.println("JSON deserialization of task object failed with message: " + e.getMessage());
            return NanoHTTPD.newFixedLengthResponse(BAD_REQUEST, HttpConstants.MIME_TYPE_TEXT_PLAIN, "Invalid body");
        }

        if (task.getName() == null) {
            return NanoHTTPD.newFixedLengthResponse(BAD_REQUEST, HttpConstants.MIME_TYPE_TEXT_PLAIN, "Task `name` is required");
        }

        Task savedTask = taskRepository.add(task);
        return asJsonResponse(CREATED, savedTask);
    }

    private NanoHTTPD.Response asJsonResponse(NanoHTTPD.Response.Status status, Object value) {
        String valueJson;
        try {
            valueJson = objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            System.err.println("Error during json serialization: " + e.getMessage());
            return NanoHTTPD.newFixedLengthResponse(INTERNAL_ERROR, HttpConstants.MIME_TYPE_TEXT_PLAIN, "Internal error");
        }
        return NanoHTTPD.newFixedLengthResponse(status, HttpConstants.MIME_TYPE_APPLICATION_JSON, valueJson);
    }
}
