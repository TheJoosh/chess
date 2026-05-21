package server;

import java.net.URI;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;

import exception.*;

import com.google.gson.Gson;

public class ServerHandler {
    private final String serverUrl;
    private final HttpClient client = HttpClient.newHttpClient();

    public ServerHandler(String url) {
        serverUrl = url;
    }

    public void clear() throws ResponseException{
        var request = buildRequest("DELETE","/db", null);
        sendRequest(request);
    }

    public void register(String body) throws ResponseException{
        var request = buildRequest("POST", "/user", body);
        sendRequest(request);
    }

    public void createGame(String body) throws ResponseException{
        var request = buildRequest("POST", "/game", body);
        sendRequest(request);
    }

    private HttpRequest buildRequest(String method, String path, Object body) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        return request.build();
    }

    private BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws ResponseException {
        try {
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }
}
