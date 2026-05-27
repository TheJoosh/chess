package server;

import service.*;
import model.*;
import results.*;

import com.google.gson.Gson;

import chess.ChessGame;
import dataaccess.*;
import exception.ResponseException;

import io.javalin.Javalin;
import io.javalin.http.Context;
import java.util.HashMap;

public class Server {

    private final Javalin javalin;
    private final AuthService authService;
    private final GameService gameService;
    private final UserService userService;
    private final DataAccess dataAccess;

    public Server() {
        this.dataAccess = new SQLDataAccess(new SQLGameAccess(), new SQLUserAccess(), new SQLAuthAccess());
        this.authService = new AuthService(dataAccess);
        this.gameService = new GameService(dataAccess);
        this.userService = new UserService(dataAccess);

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
            .delete("/db", this::clear)
            .delete("/session", this::logout)
            .post("/user", this::register)
            .post("/session", this::login)
            .post("/game", this::createGame)
            .get("/game", this::listGames)
            .put("/game", this::joinGame)
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

    private void clear(Context ctx) throws DataAccessException {
        authService.clear();
        userService.clear();
        gameService.clear();
        ctx.status(200).result(new Gson().toJson(new Result("")));
    }

    private void register(Context ctx) throws DataAccessException {
        UserData user = new Gson().fromJson(ctx.body(), UserData.class);

        if (user == null || user.username() == null || user.email() == null || user.password() == null) {
            ctx.status(400).result(new Gson().toJson(new Result("Error: bad request")));
            return;
        }

        if (!userService.getUser(user)) {
            AuthData auth = userService.register(user);
            authService.addAuth(auth);
            ctx.status(200);
            ctx.result(new Gson().toJson(auth));
        } else {
            ctx.status(403).result(new Gson().toJson(new Result("Error: already taken")));
        }
    }

    private void login(Context ctx) throws DataAccessException {
        LoginRequest user = new Gson().fromJson(ctx.body(), LoginRequest.class);

        if (user == null || user.username() == null || user.password() == null) {
            ctx.status(400).result(new Gson().toJson(new Result("Error: bad request")));
            return;
        }

        if(userService.verifyPassword(user)) {
            AuthData auth = authService.login(user);
            ctx.status(200);
            ctx.result(new Gson().toJson(auth));
            return;
        }

        ctx.status(401).result(new Gson().toJson(new Result("Error: unauthorized")));
    }

    private void logout(Context ctx) throws DataAccessException {
        String authToken = ctx.header("authorization");

        if (authToken == null) {
            ctx.status(400).result(new Gson().toJson(new Result("Error: bad request")));
            return;
        }

        if (authService.authenticate(authToken)) {
            authService.removeAuth(authToken);
            ctx.status(200);
            ctx.result(new Gson().toJson(new LogoutResult("")));
        } else {
            ctx.status(401).result(new Gson().toJson(new Result("Error: unauthorized")));
        }
    }

    private void createGame(Context ctx) throws DataAccessException {
        CreateRequest request = new Gson().fromJson(ctx.body(), CreateRequest.class);
        String authToken = ctx.header("authorization");

        if (authToken == null) {
            ctx.status(400).result(new Gson().toJson(new Result("Error: bad request")));
            return;
        }

        if (authToken != null && authService.authenticate(authToken)) {

            if (request == null || request.gameName() == null) {
                ctx.status(400).result(new Gson().toJson(new Result("Error: bad request")));
                return;
            }
            int gameID = gameService.createGame(request.gameName());
            CreateResult result = new CreateResult(gameID);
            ctx.result(new Gson().toJson(result));
            ctx.status(200);
        } else {
            ctx.status(401).result(new Gson().toJson(new Result("Error: unauthorized")));
        }
    }

    private void listGames(Context ctx) throws DataAccessException {
        String authToken = ctx.header("authorization");

        if (authToken == null) {
            ctx.status(400).result(new Gson().toJson(new Result("Error: bad request")));
            return;
        }

        if (authService.authenticate(authToken)) {
            HashMap<Integer, ListGameResult> games = gameService.listGames();
            ListGamesResults result = new ListGamesResults(games);
            ctx.status(200);
            ctx.result(new Gson().toJson(result));
        } else {
            ctx.status(401).result(new Gson().toJson(new Result("Error: unauthorized")));
        }
    }

    private void joinGame(Context ctx) throws DataAccessException {

        String authToken = ctx.header("authorization");

        if (authToken == null) {
            ctx.status(400).result(new Gson().toJson(new Result("Error: bad request")));
            return;
        }

        if (authService.authenticate(authToken)) {
            JoinRequest game = new Gson().fromJson(ctx.body(), JoinRequest.class);

            if (game == null || game.playerColor() == null || game.gameID() <= 0) {
                 ctx.status(400).result(new Gson().toJson(new Result("Error: bad request")));
                 return;
            }
            
            String username = authService.getUsername(authToken);
            ChessGame.TeamColor color = game.playerColor();
            int gameID = game.gameID();

            if (gameService.joinGame(username, color, gameID)) {
                ctx.status(200).result(new Gson().toJson(new Result("")));
            } else {
                ctx.status(403).result(new Gson().toJson(new Result("Error: already taken")));
            }
        } else {
            ctx.status(401).result(new Gson().toJson(new Result("Error: unauthorized")));
        }
    }
}
