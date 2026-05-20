package server;

import io.javalin.*;
import service.*;
import model.*;
import dataaccess.*;

public class Server {

    private final Javalin javalin;
    private final AuthService authService;
    private final GameService gameService;
    private final UserService userService;

    public Server() {

        this.authService = new AuthService(new AuthAccess());
        this.gameService = new GameService();
        this.userService = new UserService();

        javalin = Javalin.create(config -> config.staticFiles.add("web"));


    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
