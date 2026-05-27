package dataaccess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.google.gson.Gson;

import chess.ChessGame;
import model.GameList;
import model.GameData;
import model.ListGameResult;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;
import java.sql.Connection;

public class SQLGameAccess implements GameDAO {

    private int next = 1;

    public void clear() throws DataAccessException {
        var statement = "TRUNCATE TABLE games";
        executeUpdate (statement);
    }

    public GameList listGames() throws DataAccessException {
        var result = new GameList();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, json FROM games";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        GameData game = readGames(rs);
                        result.put(game.gameID(), new ListGameResult(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName()));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return result;
    }

    public boolean joinGame(String username, ChessGame.TeamColor color, int gameID) throws DataAccessException {
        var statement = "";

        try (Connection conn = DatabaseManager.getConnection()) {
            var get = "SELECT gameID, whiteUsername, blackUsername, gameName, json FROM games";
            try (PreparedStatement ps = conn.prepareStatement(get)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        if (readGames(rs).gameID() == gameID) {
                            if (color == ChessGame.TeamColor.WHITE && readGames(rs).whiteUsername() == null) {
                                statement = "UPDATE games SET whiteUsername = ? WHERE gameID = ?";
                            } else if (color == ChessGame.TeamColor.BLACK && readGames(rs).blackUsername() == null) {
                                statement = "UPDATE games SET blackUsername = ? WHERE gameID = ?";
                            } else {
                                return false;
                            }
                        }
                    }
                }
            }

            executeUpdate(statement, username, gameID);
            return true;
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
    }

    public int createGame(String game) throws DataAccessException {
        GameData newGame = new GameData(next++, null, null, game, new ChessGame());
        var statement = "INSERT INTO games (gameID, whiteUsername, blackUsername, gameName, game, json) VALUES (?, ?, ?, ?, ?, ?)";
        String json = new Gson().toJson(newGame);
        executeUpdate(statement, newGame.gameID(), newGame.whiteUsername(), newGame.blackUsername(), game, new Gson().toJson(newGame.game()), json);
        return newGame.gameID();
    }

    private GameData readGames(ResultSet rs) throws SQLException {
        var json = rs.getString("json");
        GameData game = new Gson().fromJson(json, GameData.class);
        return game;
    }

    public int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                    else if (param instanceof ChessGame.TeamColor p) ps.setString(i + 1, p.toString());
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }
}
