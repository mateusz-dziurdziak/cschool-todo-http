package pl.connectis.todo;

import fi.iki.elonen.NanoHTTPD;

import java.io.IOException;

public class TodoApp extends NanoHTTPD {

    private final RequestUrlMapper requestUrlMapper = new RequestUrlMapper();

    public static void main(String[] args) {
        try {
            new TodoApp(8080)
                    .start(5000, false);
        } catch (IOException ex) {
            System.err.println("Server was unable to start: " + ex.getMessage());
        }
    }

    public TodoApp(int port) {
        super(port);
    }

    @Override
    public Response serve(IHTTPSession session) {
        return requestUrlMapper.delegateRequest(session);
    }
}
