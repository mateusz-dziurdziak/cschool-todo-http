package pl.connectis.todo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.iki.elonen.NanoHTTPD;
import pl.connectis.todo.domain.Task;
import pl.connectis.todo.repository.InMemoryTaskRepository;
import pl.connectis.todo.repository.TaskRepository;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static fi.iki.elonen.NanoHTTPD.Response.Status.*;

public class TaskController {

    private final TaskRepository taskRepository = new InMemoryTaskRepository();

    public NanoHTTPD.Response handleGetAll(NanoHTTPD.IHTTPSession session) {
        List<Task> tasks = taskRepository.getAll();

        return asJson(tasks);
    }

    public NanoHTTPD.Response handleGetSingle(NanoHTTPD.IHTTPSession session) {
        Map<String, List<String>> parameters = session.getParameters();
        List<String> ids = parameters.getOrDefault("id", Collections.emptyList());

        if (ids.isEmpty()) {
            return NanoHTTPD.newFixedLengthResponse(BAD_REQUEST, "text/plain", "No `id` parameter");
        }

        if (ids.size() > 1) {
            return NanoHTTPD.newFixedLengthResponse(BAD_REQUEST, "text/plain", "More than one `id` parameter");
        }

        String id = ids.get(0);

        long parsedId;
        try {
            parsedId = Long.parseLong(id);
        } catch (NumberFormatException nfe) {
            return NanoHTTPD.newFixedLengthResponse(BAD_REQUEST, "text/plain", "Invalid `id`");
        }

        Task task = taskRepository.get(parsedId);

        if (task == null) {
            return NanoHTTPD.newFixedLengthResponse(NOT_FOUND, "text/plain", "Task doesn't exist");
        }

        return asJson(task);
    }

    public NanoHTTPD.Response handleAdd(NanoHTTPD.IHTTPSession session) {
        String contentLength = session.getHeaders().get("content-length");
        int contentLengthInt = Integer.parseInt(contentLength);

        byte[] buffer = new byte[contentLengthInt];

        Task task;
        try {
            session.getInputStream().read(buffer, 0, contentLengthInt);
            ObjectMapper objectMapper = new ObjectMapper();
            task = objectMapper.readValue(buffer, Task.class);
        } catch (IOException e) {
            e.printStackTrace();
            return NanoHTTPD.newFixedLengthResponse(BAD_REQUEST, "text/plain", "Bad request");
        }

        task.setCompleted(false);

        Task savedTask = taskRepository.add(task);

        return asJson(savedTask);
    }

    private NanoHTTPD.Response asJson(Object value) {
        ObjectMapper objectMapper = new ObjectMapper();
        String valueJson;
        try {
            valueJson = objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return NanoHTTPD.newFixedLengthResponse(INTERNAL_ERROR, "text/plain", "Internal error");
        }
        return NanoHTTPD.newFixedLengthResponse(OK, "application/json", valueJson);
    }
}
