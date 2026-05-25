package dataaccess;

import java.sql.*;

import chess.ChessGame;
import exception.ResponseException;
import model.AuthData;
import model.AuthList;
import model.GameList;
import model.UserData;
import model.UserList;
import results.LoginRequest;

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

    public UserList listUsers() {
        return userAccess.listUsers();
    } 

    public boolean verifyPassword(LoginRequest user) {
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
        return gameAccess.listGames();
    }

    public boolean joinGame(String username, ChessGame.TeamColor color, int gameID) throws DataAccessException {
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
              `hashedpassword` varchar(256) NOT NULL,
              `json` TEXT DEFAULT NULL,
              PRIMARY KEY (`username`),
              INDEX(email)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

            CREATE TABLE IF NOT EXISTS  authData (
              `username` varchar(256) NOT NULL,
              `token` varchar(256) NOT NULL,
              `json` TEXT DEFAULT NULL,
              PRIMARY KEY (`username`),
              INDEX(token)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

            CREATE TABLE IF NOT EXISTS  games (
              `id` int NOT NULL AUTO_INCREMENT,
              `whiteUsername` varchar(256) DEFAULT NULL,
              `blackUsername` varchar(256) DEFAULT NULL,
              `name` varchar(256) NOT NULL,
              `game` varchar(256) NOT NULL,
              `json` TEXT DEFAULT NULL,
              PRIMARY KEY (`id`),
              INDEX(name)
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

}
