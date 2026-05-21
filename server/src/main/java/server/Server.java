package server;

import io.javalin.*;
import service.*;
import model.*;

import com.google.gson.Gson;

import dataaccess.*;
import exception.ResponseException;

import io.javalin.Javalin;
import io.javalin.http.Context;

public class Server {

    private final Javalin javalin;
    private final AuthService authService;
    private final GameService gameService;
    private final UserService userService;

    public Server() {

        this.authService = new AuthService(new AuthAccess());
        this.gameService = new GameService(new GameAccess());
        this.userService = new UserService(new UserAccess());

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
            .delete("/db", this::clear)
            .delete("/session", this::logout)
            .post("user", this::register)
            .post("game", this::createGame)
            .get("game", this::listGames)
            .put("game", this::joinGame)
            .exception(ResponseException.class, this::exceptionHandler)
        ;

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void exceptionHandler(ResponseException ex, Context ctx) {
        ctx.status(ex.toHttpStatusCode());
        ctx.result(ex.toJson());
    }

    private void clear(Context ctx) throws ResponseException {
        authService.clear();
        userService.clear();
        gameService.clear();
        ctx.status(200);
    }

    private void register(Context ctx) throws ResponseException {
        UserData user = new Gson().fromJson(ctx.body(), UserData.class);
        user = userService.register(user);
        ctx.status(200);
    }

    private void logout(Context ctx) throws ResponseException {
        AuthData authToken = new Gson().fromJson(ctx.body(), AuthData.class);
        if (authService.authenticate(authToken)) {
            authService.removeAuth(authToken);
            ctx.status(200);
        } else {
            ctx.status(401);
        }
    }

    private void createGame(Context ctx) throws ResponseException {
        GameData game = new Gson().fromJson(ctx.body(), GameData.class);
        game = gameService.createGame(game);
        ctx.status(200);
    }

    private void listGames(Context ctx) throws ResponseException {
        AuthData authData = new Gson().fromJson(ctx.body(), AuthData.class);
        if (authService.authenticate(authData)) {
            gameService.listGames();
            ctx.status(200);
        } else {
            ctx.status(401);
        }
    }

    private void joinGame(Context ctx) throws ResponseException {
        GameData game = new Gson().fromJson(ctx.body(), GameData.class);
        gameService.joinGame(game);
        ctx.status(200);
    }
}
