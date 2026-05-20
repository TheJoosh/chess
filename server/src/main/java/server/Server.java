package server;

import io.javalin.*;
import service.*;
import model.*;
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
        this.gameService = new GameService();
        this.userService = new UserService();

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
            .delete("/db", this::clear)
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
        ctx.status(204);
    }
}
