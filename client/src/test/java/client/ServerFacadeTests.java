package client;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

import chess.ChessGame;
import server.Server;
import server.ServerFacade;

import exception.ResponseException;
import model.*;
import results.*;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() throws ResponseException {
        server = new Server();
        var port = server.run(8080);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:8080");
        facade.clear();
    }

    @AfterAll
    static void stopServer() throws ResponseException {
        server.stop();
    }


    @Test
    public void registerTestPositive() throws ResponseException {
        AuthData auth = facade.register(new UserData("sername", "password", "email"));

        assert(auth != null);
    }

    @Test
    public void registerTestNegative() throws ResponseException {

        assertThrows(Exception.class, () -> facade.register(new UserData(null, "password", "email")));
    }

    @Test
    public void loginTestPositive() throws ResponseException {
        facade.register(new UserData("ame", "password", "email"));
        assertDoesNotThrow(() -> facade.login(new LoginRequest("ame", "password")));
    }

    @Test
    public void loginTestNegative() throws ResponseException {
        assertThrows(Exception.class, () -> facade.login(new LoginRequest(null, "password")));
    }

    @Test
    public void logoutTestPositive() throws ResponseException {
        AuthData auth = facade.register(new UserData("jake", "password", "email"));
        assertDoesNotThrow(() -> facade.logout(auth.authToken(), null));
    }

    @Test
    public void logoutTestNegative() throws ResponseException {
        assertThrows(Exception.class, () -> facade.logout(null, null));
    }

    @Test
    public void createTestPositive() throws ResponseException {
        AuthData auth = facade.register(new UserData("sake", "password", "email"));
        assertDoesNotThrow(() -> facade.createGame(auth.authToken(), new CreateRequest(auth.authToken(), "game")));
    }

    @Test
    public void createTestNegative() throws ResponseException {
        assertThrows(Exception.class, () -> facade.createGame(null, new CreateRequest(null, "name")));
    }

    @Test
    public void listTestPositive() throws ResponseException {
        AuthData auth = facade.register(new UserData("bakery", "password", "email"));
        assertDoesNotThrow(() -> facade.listGames(auth.authToken(), null));
    }

    @Test
    public void listTestNegative() throws ResponseException {
        assertThrows(Exception.class, () -> facade.listGames(null, null));
    }

    @Test
    public void joinTestPositive() throws ResponseException {
        AuthData auth = facade.register(new UserData("bake", "password", "email"));
        facade.createGame(auth.authToken(), new CreateRequest(auth.authToken(), "game"));
        int id = facade.listGames(auth.authToken(), null).getGames().get(0).gameID();
        assertDoesNotThrow(() -> facade.joinGame(auth.authToken(), new JoinRequest(ChessGame.TeamColor.BLACK, id)));
    }

    @Test
    public void joinTestNegative() throws ResponseException {
        assertThrows(Exception.class, () -> facade.listGames(null, null));
    }
}
