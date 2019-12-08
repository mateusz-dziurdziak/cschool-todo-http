package pl.connectis.todo;

import fi.iki.elonen.NanoHTTPD;
import pl.connectis.todo.repository.TaskRepository;

public class TodoServer extends NanoHTTPD {

    private final RequestUrlMapper requestUrlMapper;

    TodoServer(int port, TaskRepository taskRepository) {
        super(port);
        requestUrlMapper = new RequestUrlMapper(taskRepository);
    }

    @Override
    public Response serve(IHTTPSession session) {
        return requestUrlMapper.delegateRequest(session);
    }
}
