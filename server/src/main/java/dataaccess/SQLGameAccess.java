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
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "TRUNCATE TABLE games";
            executeUpdate (conn, statement);
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
    }

    public GameList listGames(Connection conn) throws DataAccessException {
        var result = new GameList();
        var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games";
        try (PreparedStatement ps = conn.prepareStatement(statement)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    GameData game = readGames(rs);
                    result.put(game.gameID(), new ListGameResult(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName()));
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return result;
    }

    public boolean joinGame(String username, ChessGame.TeamColor color, int gameID) throws DataAccessException {
        var get = "SELECT * FROM games WHERE gameID = ? LIMIT 1";

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement ps = conn.prepareStatement(get)) {
            ps.setInt(1, gameID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                if (color == ChessGame.TeamColor.BLACK && rs.getString("blackUsername") == null) {
                    String statement = "UPDATE games SET blackUsername = ? WHERE gameID = ?";
                    executeUpdate(conn, statement, username, gameID);
                    return true;
                } else if (color == ChessGame.TeamColor.WHITE && rs.getString("whiteUsername") == null) {
                    String statement = "UPDATE games SET whiteUsername = ? WHERE gameID = ?";
                    executeUpdate(conn, statement, username, gameID);
                    return true;
                }
            }

            return false;
            
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
    }

    public int createGame(String game) throws DataAccessException {
        var statement = "INSERT INTO games (gameID, whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection()) {
            GameData newGame = new GameData(next++, null, null, game, new ChessGame());

            executeUpdate(conn, statement, newGame.gameID(), newGame.whiteUsername(), newGame.blackUsername(), game, new Gson().toJson(newGame.game()));
            return newGame.gameID();
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    private GameData readGames(ResultSet rs) throws SQLException {
        GameData game = new GameData(   
                                rs.getInt("gameID"), 
                                rs.getString("whiteUsername"), 
                                rs.getString("blackUsername"), 
                                rs.getString("gameName"), 
                                new Gson().fromJson(rs.getString("game"), ChessGame.class)
                            );
        return game;
    }

    public int executeUpdate(Connection conn, String statement, Object... params) throws DataAccessException {
        try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < params.length; i++) {
                Object param = params[i];
                if (param instanceof String p) ps.setString(i + 1, p);
                else if (param instanceof Integer p) ps.setInt(i + 1, p);
                else if (param instanceof ChessGame.TeamColor p) ps.setString(i + 1, p.toString());
                else if (param == null) ps.setNull(i + 1, NULL);
            }
            int updated = ps.executeUpdate();
            System.out.println("Rows updated = " + updated);
            return updated;

        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }
}
