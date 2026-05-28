package dataaccess;

import org.junit.jupiter.api.*;

import chess.ChessGame;
import model.AuthData;
import model.ListGameResult;
import model.UserData;
import results.LoginRequest;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DAOTests {
    static final DataAccess ACCESS = new SQLDataAccess(new SQLGameAccess(), new SQLUserAccess(), new SQLAuthAccess());

    @AfterEach
    void clearData() throws DataAccessException {
        ACCESS.clear();
    }

    @Test
    void getUserTestPositive() throws DataAccessException {
        ACCESS.register(new UserData("username", "password", "email"));
        boolean gotUser = ACCESS.getUser(new UserData("username", "password", "email"));

        assert(gotUser);
    }

    @Test
    void getUserTestNegative() throws DataAccessException {
        ACCESS.register(new UserData("username", "password", "email"));
        boolean gotUser = ACCESS.getUser(new UserData("yusername", "password", "email"));

        assert(!gotUser);
    }

    @Test
    void listUsersTest() throws DataAccessException {
        ACCESS.register(new UserData("username", "password", "email"));
        ACCESS.register(new UserData("yusername", "password", "gmail"));

        assertEquals(2, ACCESS.listUsers().size());
    }

    @Test
    void listUsersTestEmpty() throws DataAccessException {
        assertEquals(0, ACCESS.listUsers().size());
    }

    @Test
    void verifyPasswordTestPositive() throws DataAccessException {
        ACCESS.register(new UserData("username", "password", "email"));

        boolean verified = ACCESS.verifyPassword(new LoginRequest("username", "password"));
        assert(verified);
    }

    @Test
    void verifyPasswordTestNegative() throws DataAccessException {
        ACCESS.register(new UserData("username", "password", "email"));

        boolean verified1 = ACCESS.verifyPassword(new LoginRequest("yusername", "password"));
        boolean verified2 = ACCESS.verifyPassword(new LoginRequest("username", "pasword"));
        assert(!verified1 && !verified2);
    }

    @Test
    void registerTestPositive() throws DataAccessException {
        var user = new UserData("usernam", "password", "mail");
        AuthData auth = ACCESS.register(user);
        
        assertEquals("usernam", auth.username());
    }

    @Test
    void registerTestNegative() throws DataAccessException {
        var nullName = new UserData(null, "password", "email");
        var nullEmail = new UserData("yusername", "password", null);
        
        assertThrows(DataAccessException.class, () -> ACCESS.register(nullName));
        assertThrows(DataAccessException.class, () -> ACCESS.register(nullEmail));
        assertThrows(DataAccessException.class, () -> ACCESS.register(null));
    }

    @Test
    void addAuthPositive() throws DataAccessException {
        String authToken = UUID.randomUUID().toString();
        ACCESS.addAuth(new AuthData(authToken, "username"));

        assert(ACCESS.listAuth().contains(authToken));
    }

    @Test
    void addAuthNegative() throws DataAccessException {
        AuthData nullToken = new AuthData(null, "username");
        String authToken = UUID.randomUUID().toString();
        AuthData nullName = new AuthData(authToken, null);

        assertThrows(DataAccessException.class, () -> ACCESS.addAuth(nullToken));
        assertThrows(DataAccessException.class, () -> ACCESS.addAuth(nullName));
        assertThrows(DataAccessException.class, () -> ACCESS.addAuth(null));
    }

    @Test
    void removeAuthPositive() throws DataAccessException {
        String authToken = UUID.randomUUID().toString();
        ACCESS.addAuth(new AuthData(authToken, "username"));
        assert(ACCESS.listAuth().contains(authToken));

        ACCESS.removeAuth(authToken);

        assert(!ACCESS.listAuth().contains(authToken));
    }

    @Test
    void removeAuthNegative() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> ACCESS.removeAuth(null));
    }

    @Test
    void authenticateTestPositive() throws DataAccessException {
        AuthData auth = ACCESS.register(new UserData("username", "password", "email"));
        ACCESS.addAuth(auth);

        assert(ACCESS.authenticate(auth.authToken()));
    }

    @Test
    void authenticateTestNegative() throws DataAccessException {
        assert(!ACCESS.authenticate(UUID.randomUUID().toString()));
    }

    @Test
    void loginTestPositive() throws DataAccessException {
        ACCESS.register(new UserData("username", "password", "email"));
        var user = new LoginRequest("username", "password");
        AuthData auth = ACCESS.login(user);
        
        assertEquals("username", auth.username());
    }

    @Test
    void loginTestNegative() throws DataAccessException {
        var nullName = new LoginRequest(null, "password");
        
        assertThrows(DataAccessException.class, () -> ACCESS.login(nullName));
    }

    @Test
    void listAuthTest() throws DataAccessException {
        ACCESS.addAuth(new AuthData(UUID.randomUUID().toString(), "username"));
        ACCESS.addAuth(new AuthData(UUID.randomUUID().toString(), "yusername"));

        assertEquals(2, ACCESS.listAuth().size());
    }

    @Test
    void listAuthTestEmpty() throws DataAccessException {
        assertEquals(0, ACCESS.listAuth().size());
    }

    @Test
    void getUsernameTestPositive() throws DataAccessException {
        AuthData auth = ACCESS.register(new UserData("username", "password", "email"));
        ACCESS.addAuth(auth);
        String username = ACCESS.getUsername(auth.authToken());

        assertEquals(username, "username");
    }

    @Test
    void getUsernameTestNegative() throws DataAccessException {

        assertThrows(DataAccessException.class, () -> ACCESS.getUsername(null));

        AuthData auth = ACCESS.register(new UserData("username", "password", "email"));
        ACCESS.addAuth(auth);
        assertThrows(DataAccessException.class, () -> ACCESS.getUsername(UUID.randomUUID().toString()));
    }

    @Test
    void listGamesTest() throws DataAccessException {
        ACCESS.createGame("game1");
        ACCESS.createGame("game2");

        assertEquals(2, ACCESS.listGames().size());
    }

    @Test
    void listGamesTestEmpty() throws DataAccessException {
        assertEquals(0, ACCESS.listGames().size());
    }

    @Test
    void joinGamePositive() throws DataAccessException {
        int gameID = ACCESS.createGame("game");

        assert(ACCESS.joinGame("username", ChessGame.TeamColor.BLACK, gameID));
    }

    @Test
    void joinGameNegative() throws DataAccessException {
        int gameID = ACCESS.createGame("game");

        ACCESS.joinGame("username", ChessGame.TeamColor.BLACK, gameID);

        assert(!ACCESS.joinGame("yusername", ChessGame.TeamColor.BLACK, gameID));
    }

    @Test
    void createGamePositive() throws DataAccessException {
        ACCESS.createGame("game");
        ListGameResult game = new ListGameResult(1, null, null, "game");

        assert(ACCESS.listGames().containsValue(game));
    }

    @Test
    void createGameNegative() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> ACCESS.createGame(null));
    }

    @Test
    void clearTest() throws DataAccessException {
        ACCESS.register(new UserData("username", "password", "email"));
        ACCESS.createGame(new ChessGame().toString());
        ACCESS.addAuth(new AuthData(UUID.randomUUID().toString(), "username"));

        ACCESS.clear();

        assertEquals(0, ACCESS.listGames().size());
        assertEquals(0, ACCESS.listUsers().size());
        assertEquals(0, ACCESS.listAuth().size());
    }
}
