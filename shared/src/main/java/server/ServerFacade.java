package server;

import java.net.URI;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;

import exception.*;
import model.*;
import results.*;

import com.google.gson.Gson;

public class ServerFacade {
    private final String serverUrl;
    private final HttpClient client = HttpClient.newHttpClient();

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public void clear() throws ResponseException{
        var request = buildRequest("DELETE","/db", null, null);
        sendRequest(request);
    }

    public AuthData register(Object body) throws ResponseException{
        var request = buildRequest("POST", "/user", null, body);
        var response = sendRequest(request);
        return handleResponse(response, AuthData.class);
    }

    public AuthData login(Object body) throws ResponseException{
        var request = buildRequest("POST", "/session", null, body);
        var response = sendRequest(request);
        return handleResponse(response, AuthData.class);
    }

    public void logout(String header, String body) throws ResponseException{
        var request = buildRequest("DELETE", "/session", header, body);
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    public void createGame(String header, Object body) throws ResponseException{
        var request = buildRequest("POST", "/game", header, body);
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    public ListGamesResults listGames(String header, String body) throws ResponseException {
        var request = buildRequest("GET", "/game", header, body);
        var response = sendRequest(request);
        return handleResponse(response, ListGamesResults.class);
    }

    public GameData joinGame(String header, Object body) throws ResponseException{
        var request = buildRequest("PUT", "/game", header, body);
        var response = sendRequest(request);
        return handleResponse(response, GameData.class);
    }

    private HttpRequest buildRequest(String method, String path, String header, Object body) {
            var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));

            if (body != null) {
                request.setHeader("Content-Type", "application/json");
            }
            if (header != null) {
                request.setHeader("authorization", header);
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
            throw new ResponseException(ResponseException.Code.BadRequest, ex.getMessage());
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
