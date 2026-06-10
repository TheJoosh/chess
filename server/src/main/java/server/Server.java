package server;

import service.*;
import model.*;
import results.*;

import com.google.gson.Gson;

import chess.ChessGame;
import websocket.*;
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
    private final WebSocketHandler webSocket;

    public Server() {
        this.dataAccess = new SQLDataAccess(new SQLGameAccess(), new SQLUserAccess(), new SQLAuthAccess());
        this.authService = new AuthService(dataAccess);
        this.gameService = new GameService(dataAccess);
        this.userService = new UserService(dataAccess);

        this.webSocket = new WebSocketHandler();

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
            .delete("/db", this::clear)
            .delete("/session", this::logout)
            .post("/user", this::register)
            .post("/session", this::login)
            .post("/game", this::createGame)
            .get("/game", this::listGames)
            .put("/game", this::joinGame)
            .exception(ResponseException.class, this::exceptionHandler)
            .ws("/ws", ws -> {
                    ws.onConnect(webSocket);
                    ws.onMessage(webSocket);
                    ws.onClose(webSocket);
                });
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
        try {
            authService.clear();
            userService.clear();
            gameService.clear();
            ctx.status(200).result(new Gson().toJson(new Result("", null)));
        } catch (DataAccessException e) {
            ctx.status(500).result(new Gson().toJson(new Result("Error: bad request", null)));
        }
    }

    private AuthData register(Context ctx) throws DataAccessException {
        try {
            UserData user = new Gson().fromJson(ctx.body(), UserData.class);

            if (user == null || user.username() == null || user.email() == null || user.password() == null) {
                ctx.status(400).result(new Gson().toJson(new Result("Error: bad request", null)));
                return null;
            }

            if (!userService.getUser(user)) {
                AuthData auth = userService.register(user);
                authService.addAuth(auth);
                ctx.status(200);
                ctx.result(new Gson().toJson(auth));
                return auth;
            } else {
                ctx.status(403).result(new Gson().toJson(new Result("Error: already taken", null)));
                return null;
            }
        } catch (DataAccessException e) {
            ctx.status(500).result(new Gson().toJson(new Result("Error: bad request", null)));
            return null;
        }
    }

    private AuthData login(Context ctx) throws DataAccessException {
        try {
            LoginRequest user = new Gson().fromJson(ctx.body(), LoginRequest.class);

            if (user == null || user.username() == null || user.password() == null) {
                ctx.status(400).result(new Gson().toJson(new Result("Error: bad request", null)));
                return null;
            }

            if(userService.verifyPassword(user)) {
                AuthData auth = authService.login(user);
                ctx.status(200);
                ctx.result(new Gson().toJson(auth));
                return auth;
            }

            ctx.status(401).result(new Gson().toJson(new Result("Error: unauthorized", null)));
            return null;
        } catch (DataAccessException e) {
            ctx.status(500).result(new Gson().toJson(new Result("Error: bad request", null)));
            return null;
        }
    }

    private void logout(Context ctx) throws DataAccessException {
        try {
            String authToken = ctx.header("authorization");

            if (authToken == null) {
                ctx.status(400).result(new Gson().toJson(new Result("Error: bad request", null)));
                return;
            }

            if (authService.authenticate(authToken)) {
                authService.removeAuth(authToken);
                ctx.status(200);
                ctx.result(new Gson().toJson(new LogoutResult("")));
            } else {
                ctx.status(401).result(new Gson().toJson(new Result("Error: unauthorized", null)));
            }
        } catch (DataAccessException e) {
            ctx.status(500).result(new Gson().toJson(new Result("Error: bad request", null)));
        }
    }

    private void createGame(Context ctx) throws DataAccessException {
        try {
            CreateRequest request = new Gson().fromJson(ctx.body(), CreateRequest.class);
            String authToken = ctx.header("Authorization");

            if (authToken == null) {
                ctx.status(400).result(new Gson().toJson(new Result("Error: bad request", null)));
                return;
            }

            if (authToken != null && authService.authenticate(authToken)) {

                if (request == null || request.gameName() == null) {
                    ctx.status(400).result(new Gson().toJson(new Result("Error: bad request", null)));
                    return;
                }
                int gameID = gameService.createGame(request.gameName());
                CreateResult result = new CreateResult(gameID);
                ctx.result(new Gson().toJson(result));
                ctx.status(200);
            } else {
                ctx.status(401).result(new Gson().toJson(new Result("Error: unauthorized", null)));
            }
        } catch (DataAccessException e) {
            ctx.status(500).result(new Gson().toJson(new Result("Error: bad request", null)));
        }
    }

    private void listGames(Context ctx) throws DataAccessException {
        try {
            String authToken = ctx.header("authorization");

            if (authToken == null) {
                ctx.status(400).result(new Gson().toJson(new Result("Error: bad request", null)));
                return;
            }

            if (authService.authenticate(authToken)) {
                HashMap<Integer, GameData> games = gameService.listGames();
                ListGamesResults result = new ListGamesResults(games);
                ctx.status(200);
                ctx.result(new Gson().toJson(result));
            } else {
                ctx.status(401).result(new Gson().toJson(new Result("Error: unauthorized", null)));
            }
        } catch (DataAccessException e) {
            ctx.status(500).result(new Gson().toJson(new Result("Error: bad request", null)));
        }
    }

    private GameData joinGame(Context ctx) throws DataAccessException {
        try {
            String authToken = ctx.header("authorization");

            if (authToken == null) {
                ctx.status(400).result(new Gson().toJson(new Result("Error: bad request", null)));
                return null;
            }

            if (authService.authenticate(authToken)) {
                JoinRequest game = new Gson().fromJson(ctx.body(), JoinRequest.class);

                if (game == null || game.playerColor() == null || game.gameID() <= 0) {
                    ctx.status(400).result(new Gson().toJson(new Result("Error: bad request", null)));
                    return null;
                }
                
                String username = authService.getUsername(authToken);
                ChessGame.TeamColor color = game.playerColor();
                int gameID = game.gameID();

                Result result = gameService.joinGame(username, color, gameID);

                if (result.getMessage() == null) {
                    ctx.status(200).result(new Gson().toJson(new Result("", null)));
                    return result.getGame();
                } else {
                    ctx.status(403).result(new Gson().toJson(new Result("Error: already taken", null)));
                    return null;
                }
            } else {
                ctx.status(401).result(new Gson().toJson(new Result("Error: unauthorized", null)));
                return null;
            }
        } catch (DataAccessException e) {
            ctx.status(500).result(new Gson().toJson(new Result("Error: bad request", null)));
            return null;
        }
    }
}
