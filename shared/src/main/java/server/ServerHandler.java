package server;

import java.net.URI;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;

import exception.*;
import model.*;

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
        var response = sendRequest(request);
        handleResponse(response, UserData.class);
    }

    public void login(String body) throws ResponseException{
        var request = buildRequest("POST", "/session", body);
        sendRequest(request);
    }

    public void logout(String body) throws ResponseException{
        var request = buildRequest("DELETE", "/session", body);
        sendRequest(request);
    }

    public void createGame(String body) throws ResponseException{
        var request = buildRequest("POST", "/game", body);
        sendRequest(request);
    }

    public void listGames(String body) throws ResponseException {
        var request = buildRequest("GET", "/game", body);
        sendRequest(request);
    }

    public void joinGame(String body) throws ResponseException{
        var request = buildRequest("PUT", "/game", body);
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

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws ResponseException {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                throw ResponseException.fromJson(body);
            }

            throw new ResponseException(ResponseException.fromHttpStatusCode(status), "other failure: " + status);
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
