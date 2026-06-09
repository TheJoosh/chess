package dataaccess;

import java.sql.*;

import chess.ChessGame;
import model.AuthData;
import model.AuthList;
import model.GameList;
import model.UserData;
import model.UserList;
import model.GameData;
import results.LoginRequest;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLDataAccess implements DataAccess {

    private final SQLGameAccess gameAccess;
    private final SQLUserAccess userAccess;
    private final SQLAuthAccess authAccess;

    public SQLDataAccess(SQLGameAccess gameAccess, SQLUserAccess userAccess, SQLAuthAccess authAccess) {
        this.authAccess = authAccess;
        this.userAccess = userAccess;
        this.gameAccess = gameAccess;
        try {
            configureDatabase();
        } catch(DataAccessException ex) {}
    }

    public void clear() throws DataAccessException {
        gameAccess.clear();
        userAccess.clear();
        authAccess.clear();
    }

    public boolean getUser(UserData user) throws DataAccessException {
        return userAccess.getUser(user);
    }

    public UserList listUsers() throws DataAccessException {
        return userAccess.listUsers();
    } 

    public boolean verifyPassword(LoginRequest user) throws DataAccessException {
        return userAccess.verifyPassword(user);
    }

    public AuthData register (UserData user) throws DataAccessException {
        return userAccess.register(user);
    }

    public void addAuth(AuthData authData) throws DataAccessException {
        authAccess.addAuth(authData);
    }

    public void removeAuth(String token) throws DataAccessException { 
        authAccess.removeAuth(token);
    }

    public boolean authenticate(String token) throws DataAccessException {
        return authAccess.authenticate(token);
    }

    public AuthData login(LoginRequest user) throws DataAccessException {
        return authAccess.login(user);
    }

    public AuthList listAuth() throws DataAccessException {
        return authAccess.listAuth();
    }

    public String getUsername(String auth) throws DataAccessException {
        return authAccess.getUsername(auth);
    }

    public GameList listGames() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            return gameAccess.listGames(conn);
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
    }

    public GameData joinGame(String username, ChessGame.TeamColor color, int gameID) throws DataAccessException {
        return gameAccess.joinGame(username, color, gameID);
    }

    public int createGame(String game) throws DataAccessException {
        return gameAccess.createGame(game);
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  users (
              `username` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              PRIMARY KEY (`username`),
              INDEX(email)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
            """,
                    
            """
            CREATE TABLE IF NOT EXISTS  authData (
              `authToken` varchar(256) NOT NULL,
              `username` varchar(256) NOT NULL,
              PRIMARY KEY (`authToken`),
              INDEX(username)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
            """,
                    
            """
            CREATE TABLE IF NOT EXISTS  games (
              `gameID` int NOT NULL AUTO_INCREMENT,
              `whiteUsername` varchar(256) DEFAULT NULL,
              `blackUsername` varchar(256) DEFAULT NULL,
              `gameName` varchar(256) NOT NULL,
              `game` TEXT NOT NULL,
              PRIMARY KEY (`gameID`),
              INDEX(gameName)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

    public static int executeUpdate(Connection conn, String statement, Object... params) throws DataAccessException {
        try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < params.length; i++) {
                Object param = params[i];
                if (param instanceof String p) {
                    ps.setString(i + 1, p);
                } else if (param instanceof Integer p) {
                    ps.setInt(i + 1, p);
                } else if (param instanceof ChessGame.TeamColor p) {
                    ps.setString(i + 1, p.toString());
                } else if (param == null) {
                    ps.setNull(i + 1, NULL);
                }
            }
            int updated = ps.executeUpdate();
            System.out.println("Rows updated = " + updated);
            return updated;

        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }
}
