package service;

import java.util.UUID;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import chess.ChessGame;
import dataaccess.AuthAccess;
import dataaccess.GameAccess;
import dataaccess.UserAccess;
import dataaccess.DataAccessException;
import model.*;
import results.LoginRequest;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceTests {
    static final AuthService authService = new AuthService(new AuthAccess());
    static final UserService userService = new UserService(new UserAccess());
    static final GameService gameService = new GameService(new GameAccess());

    @BeforeEach
    void clearData() throws DataAccessException {
        authService.clear();
        userService.clear();
        gameService.clear();
    }

    @Test
    void registerTestPositive() throws DataAccessException {
        var user = new UserData("username", "password", "email");
        AuthData auth = userService.register(user);
        
        assertEquals("username", auth.username());
    }

    @Test
    void registerTestNegative() throws DataAccessException {
        var nullName = new UserData(null, "password", "email");
        var nullPassword = new UserData("username", null, "gmail");
        var nullEmail = new UserData("yusername", "password", null);
        
        assertThrows(DataAccessException.class, () -> userService.register(nullName));
        assertThrows(DataAccessException.class, () -> userService.register(nullPassword));
        assertThrows(DataAccessException.class, () -> userService.register(nullEmail));
        assertThrows(DataAccessException.class, () -> userService.register(null));
    }

    @Test
    void getUserTestPositive() throws DataAccessException {
        userService.register(new UserData("username", "password", "email"));
        boolean gotUser = userService.getUser(new UserData("username", "password", "email"));

        assert(gotUser);
    }

    @Test
    void getUserTestNegative() throws DataAccessException {
        userService.register(new UserData("username", "password", "email"));
        boolean gotUser = userService.getUser(new UserData("yusername", "password", "email"));

        assert(!gotUser);
    }

    @Test
    void verifyPasswordTestPositive() throws DataAccessException {
        userService.register(new UserData("username", "password", "email"));

        boolean verified = userService.verifyPassword(new LoginRequest("username", "password"));
        assert(verified);
    }

    @Test
    void verifyPasswordTestNegative() throws DataAccessException {
        userService.register(new UserData("username", "password", "email"));

        boolean verified1 = userService.verifyPassword(new LoginRequest("yusername", "password"));
        boolean verified2 = userService.verifyPassword(new LoginRequest("username", "pasword"));
        assert(!verified1 && !verified2);
    }

    @Test
    void listUsersTest() throws DataAccessException {
        userService.register(new UserData("username", "password", "email"));
        userService.register(new UserData("yusername", "password", "gmail"));

        assertEquals(2, userService.listUsers().size());
    }

    @Test
    void listUsersTestEmpty() throws DataAccessException {
        assertEquals(0, userService.listUsers().size());
    }

    @Test
    void authenticateTestPositive() throws DataAccessException {
        AuthData auth = userService.register(new UserData("username", "password", "email"));
        authService.addAuth(auth);

        assert(authService.authenticate(auth.authToken()));
    }

    @Test
    void authenticateTestNegative() throws DataAccessException {
        assert(!authService.authenticate(UUID.randomUUID().toString()));
    }

    @Test
    void addAuthPositive() throws DataAccessException {
        String authToken = UUID.randomUUID().toString();
        authService.addAuth(new AuthData(authToken, "username"));

        assert(authService.listAuth().contains("username"));
    }

    @Test
    void addAuthNegative() throws DataAccessException {
        AuthData nullToken = new AuthData(null, "username");
        String authToken = UUID.randomUUID().toString();
        AuthData nullName = new AuthData(authToken, null);

        assertThrows(DataAccessException.class, () -> authService.addAuth(nullToken));
        assertThrows(DataAccessException.class, () -> authService.addAuth(nullName));
        assertThrows(DataAccessException.class, () -> authService.addAuth(null));
    }

    @Test
    void removeAuthPositive() throws DataAccessException {
        String authToken = UUID.randomUUID().toString();
        authService.addAuth(new AuthData(authToken, "username"));
        assert(authService.listAuth().contains("username"));

        authService.removeAuth(authToken);

        assert(!authService.listAuth().contains("username"));
    }

    @Test
    void removeAuthNegative() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> authService.removeAuth(UUID.randomUUID().toString()));
    }

    @Test
    void listAuthTest() throws DataAccessException {
        authService.addAuth(new AuthData(UUID.randomUUID().toString(), "username"));
        authService.addAuth(new AuthData(UUID.randomUUID().toString(), "yusername"));

        assertEquals(2, authService.listAuth().size());
    }

    @Test
    void listAuthTestEmpty() throws DataAccessException {
        assertEquals(0, authService.listAuth().size());
    }

    @Test
    void loginTestPositive() throws DataAccessException {
        userService.register(new UserData("username", "password", "email"));
        var user = new LoginRequest("username", "password");
        AuthData auth = authService.login(user);
        
        assertEquals("username", auth.username());
    }

    @Test
    void LoginTestNegative() throws DataAccessException {
        var nullName = new LoginRequest(null, "password");
        var nullPassword = new LoginRequest("username", null);
        
        assertThrows(DataAccessException.class, () -> authService.login(nullName));
        assertThrows(DataAccessException.class, () -> authService.login(nullPassword));
        assertThrows(DataAccessException.class, () -> authService.login(null));
    }

    @Test
    void getUsernameTestPositive() throws DataAccessException {
        AuthData auth = userService.register(new UserData("username", "password", "email"));
        authService.addAuth(auth);
        String username = authService.getUsername(auth.authToken());

        assertEquals(username, "username");
    }

    @Test
    void getUsernameTestNegative() throws DataAccessException {

        assertThrows(DataAccessException.class, () -> authService.getUsername(null));

        AuthData auth = userService.register(new UserData("username", "password", "email"));
        authService.addAuth(auth);
        assertThrows(DataAccessException.class, () -> authService.getUsername(UUID.randomUUID().toString()));
    }

    @Test
    void listGamesTest() throws DataAccessException {
        gameService.createGame("game1");
        gameService.createGame("game2");

        assertEquals(2, gameService.listGames().size());
    }

    @Test
    void listGamesTestEmpty() throws DataAccessException {
        assertEquals(0, gameService.listGames().size());
    }

    @Test
    void createGamePositive() throws DataAccessException {
        gameService.createGame("game");
        ListGameResult game = new ListGameResult(1, null, null, "game");

        assert(gameService.listGames().contains(game));
    }

    @Test
    void createGameNegative() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> gameService.createGame(null));
    }

    @Test
    void joinGamePositive() throws DataAccessException {
        int gameID = gameService.createGame("game");

        assert(gameService.joinGame("username", ChessGame.TeamColor.BLACK, gameID));
    }

    @Test
    void joinGameNegative() throws DataAccessException {
        int gameID = gameService.createGame("game");

        assertThrows(DataAccessException.class, () -> gameService.joinGame(null, ChessGame.TeamColor.BLACK, gameID));
        assertThrows(DataAccessException.class, () -> gameService.joinGame("username", null, gameID));

        gameService.joinGame("username", ChessGame.TeamColor.BLACK, gameID);

        assert(!gameService.joinGame("yusername", ChessGame.TeamColor.BLACK, gameID));
    }

    @Test
    void clearTest() throws DataAccessException {
        userService.register(new UserData("username", "password", "email"));
        gameService.createGame(new ChessGame().toString());
        authService.addAuth(new AuthData(UUID.randomUUID().toString(), "username"));

        userService.clear();
        gameService.clear();
        authService.clear();
        assertEquals(0, gameService.listGames().size());
        assertEquals(0, userService.listUsers().size());
        assertEquals(0, authService.listAuth().size());
    }

}
