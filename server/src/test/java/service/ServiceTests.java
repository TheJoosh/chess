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
    static final AuthService AUTH_SERVICE = new AuthService(new AuthAccess());
    static final UserService USER_SERVICE = new UserService(new UserAccess());
    static final GameService GAME_SERVICE = new GameService(new GameAccess());

    @BeforeEach
    void clearData() throws DataAccessException {
        AUTH_SERVICE.clear();
        USER_SERVICE.clear();
        GAME_SERVICE.clear();
    }

    @Test
    void registerTestPositive() throws DataAccessException {
        var user = new UserData("username", "password", "email");
        AuthData auth = USER_SERVICE.register(user);
        
        assertEquals("username", auth.username());
    }

    @Test
    void registerTestNegative() throws DataAccessException {
        var nullName = new UserData(null, "password", "email");
        var nullPassword = new UserData("username", null, "gmail");
        var nullEmail = new UserData("yusername", "password", null);
        
        assertThrows(DataAccessException.class, () -> USER_SERVICE.register(nullName));
        assertThrows(DataAccessException.class, () -> USER_SERVICE.register(nullPassword));
        assertThrows(DataAccessException.class, () -> USER_SERVICE.register(nullEmail));
        assertThrows(DataAccessException.class, () -> USER_SERVICE.register(null));
    }

    @Test
    void getUserTestPositive() throws DataAccessException {
        USER_SERVICE.register(new UserData("username", "password", "email"));
        boolean gotUser = USER_SERVICE.getUser(new UserData("username", "password", "email"));

        assert(gotUser);
    }

    @Test
    void getUserTestNegative() throws DataAccessException {
        USER_SERVICE.register(new UserData("username", "password", "email"));
        boolean gotUser = USER_SERVICE.getUser(new UserData("yusername", "password", "email"));

        assert(!gotUser);
    }

    @Test
    void verifyPasswordTestPositive() throws DataAccessException {
        USER_SERVICE.register(new UserData("username", "password", "email"));

        boolean verified = USER_SERVICE.verifyPassword(new LoginRequest("username", "password"));
        assert(verified);
    }

    @Test
    void verifyPasswordTestNegative() throws DataAccessException {
        USER_SERVICE.register(new UserData("username", "password", "email"));

        boolean verified1 = USER_SERVICE.verifyPassword(new LoginRequest("yusername", "password"));
        boolean verified2 = USER_SERVICE.verifyPassword(new LoginRequest("username", "pasword"));
        assert(!verified1 && !verified2);
    }

    @Test
    void listUsersTest() throws DataAccessException {
        USER_SERVICE.register(new UserData("username", "password", "email"));
        USER_SERVICE.register(new UserData("yusername", "password", "gmail"));

        assertEquals(2, USER_SERVICE.listUsers().size());
    }

    @Test
    void listUsersTestEmpty() throws DataAccessException {
        assertEquals(0, USER_SERVICE.listUsers().size());
    }

    @Test
    void authenticateTestPositive() throws DataAccessException {
        AuthData auth = USER_SERVICE.register(new UserData("username", "password", "email"));
        AUTH_SERVICE.addAuth(auth);

        assert(AUTH_SERVICE.authenticate(auth.authToken()));
    }

    @Test
    void authenticateTestNegative() throws DataAccessException {
        assert(!AUTH_SERVICE.authenticate(UUID.randomUUID().toString()));
    }

    @Test
    void addAuthPositive() throws DataAccessException {
        String authToken = UUID.randomUUID().toString();
        AUTH_SERVICE.addAuth(new AuthData(authToken, "username"));

        assert(AUTH_SERVICE.listAuth().contains("username"));
    }

    @Test
    void addAuthNegative() throws DataAccessException {
        AuthData nullToken = new AuthData(null, "username");
        String authToken = UUID.randomUUID().toString();
        AuthData nullName = new AuthData(authToken, null);

        assertThrows(DataAccessException.class, () -> AUTH_SERVICE.addAuth(nullToken));
        assertThrows(DataAccessException.class, () -> AUTH_SERVICE.addAuth(nullName));
        assertThrows(DataAccessException.class, () -> AUTH_SERVICE.addAuth(null));
    }

    @Test
    void removeAuthPositive() throws DataAccessException {
        String authToken = UUID.randomUUID().toString();
        AUTH_SERVICE.addAuth(new AuthData(authToken, "username"));
        assert(AUTH_SERVICE.listAuth().contains("username"));

        AUTH_SERVICE.removeAuth(authToken);

        assert(!AUTH_SERVICE.listAuth().contains("username"));
    }

    @Test
    void removeAuthNegative() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> AUTH_SERVICE.removeAuth(UUID.randomUUID().toString()));
    }

    @Test
    void listAuthTest() throws DataAccessException {
        AUTH_SERVICE.addAuth(new AuthData(UUID.randomUUID().toString(), "username"));
        AUTH_SERVICE.addAuth(new AuthData(UUID.randomUUID().toString(), "yusername"));

        assertEquals(2, AUTH_SERVICE.listAuth().size());
    }

    @Test
    void listAuthTestEmpty() throws DataAccessException {
        assertEquals(0, AUTH_SERVICE.listAuth().size());
    }

    @Test
    void loginTestPositive() throws DataAccessException {
        USER_SERVICE.register(new UserData("username", "password", "email"));
        var user = new LoginRequest("username", "password");
        AuthData auth = AUTH_SERVICE.login(user);
        
        assertEquals("username", auth.username());
    }

    @Test
    void loginTestNegative() throws DataAccessException {
        var nullName = new LoginRequest(null, "password");
        var nullPassword = new LoginRequest("username", null);
        
        assertThrows(DataAccessException.class, () -> AUTH_SERVICE.login(nullName));
        assertThrows(DataAccessException.class, () -> AUTH_SERVICE.login(nullPassword));
        assertThrows(DataAccessException.class, () -> AUTH_SERVICE.login(null));
    }

    @Test
    void getUsernameTestPositive() throws DataAccessException {
        AuthData auth = USER_SERVICE.register(new UserData("username", "password", "email"));
        AUTH_SERVICE.addAuth(auth);
        String username = AUTH_SERVICE.getUsername(auth.authToken());

        assertEquals(username, "username");
    }

    @Test
    void getUsernameTestNegative() throws DataAccessException {

        assertThrows(DataAccessException.class, () -> AUTH_SERVICE.getUsername(null));

        AuthData auth = USER_SERVICE.register(new UserData("username", "password", "email"));
        AUTH_SERVICE.addAuth(auth);
        assertThrows(DataAccessException.class, () -> AUTH_SERVICE.getUsername(UUID.randomUUID().toString()));
    }

    @Test
    void listGamesTest() throws DataAccessException {
        GAME_SERVICE.createGame("game1");
        GAME_SERVICE.createGame("game2");

        assertEquals(2, GAME_SERVICE.listGames().size());
    }

    @Test
    void listGamesTestEmpty() throws DataAccessException {
        assertEquals(0, GAME_SERVICE.listGames().size());
    }

    @Test
    void createGamePositive() throws DataAccessException {
        GAME_SERVICE.createGame("game");
        ListGameResult game = new ListGameResult(1, null, null, "game");

        assert(GAME_SERVICE.listGames().contains(game));
    }

    @Test
    void createGameNegative() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> GAME_SERVICE.createGame(null));
    }

    @Test
    void joinGamePositive() throws DataAccessException {
        int gameID = GAME_SERVICE.createGame("game");

        assert(GAME_SERVICE.joinGame("username", ChessGame.TeamColor.BLACK, gameID));
    }

    @Test
    void joinGameNegative() throws DataAccessException {
        int gameID = GAME_SERVICE.createGame("game");

        assertThrows(DataAccessException.class, () -> GAME_SERVICE.joinGame(null, ChessGame.TeamColor.BLACK, gameID));
        assertThrows(DataAccessException.class, () -> GAME_SERVICE.joinGame("username", null, gameID));

        GAME_SERVICE.joinGame("username", ChessGame.TeamColor.BLACK, gameID);

        assert(!GAME_SERVICE.joinGame("yusername", ChessGame.TeamColor.BLACK, gameID));
    }

    @Test
    void clearTest() throws DataAccessException {
        USER_SERVICE.register(new UserData("username", "password", "email"));
        GAME_SERVICE.createGame(new ChessGame().toString());
        AUTH_SERVICE.addAuth(new AuthData(UUID.randomUUID().toString(), "username"));

        USER_SERVICE.clear();
        GAME_SERVICE.clear();
        AUTH_SERVICE.clear();
        assertEquals(0, GAME_SERVICE.listGames().size());
        assertEquals(0, USER_SERVICE.listUsers().size());
        assertEquals(0, AUTH_SERVICE.listAuth().size());
    }

}
