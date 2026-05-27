package dataaccess;

import java.util.HashMap;

import model.GameList;
import model.GameData;
import model.ListGameResult;
import chess.ChessGame;
import java.sql.Connection;

public class GameAccess implements GameDAO {

    final private HashMap<Integer, ListGameResult> games = new HashMap<>();
    private int next = 1;

    public GameList listGames(Connection conn) throws DataAccessException {
        GameList newList = new GameList();
        newList.putAll(games);
        return newList;
    } 

    public void clear() throws DataAccessException {
        games.clear();
    }

    public boolean joinGame(String username, ChessGame.TeamColor color, int gameID) throws DataAccessException {
        ListGameResult game = games.get(gameID);

        if (color == ChessGame.TeamColor.WHITE && game.whiteUsername() == null) {
            games.replace(gameID, new ListGameResult(gameID, username, game.blackUsername(), game.gameName()));
            return true;

        } else if (color == ChessGame.TeamColor.BLACK && game.blackUsername() == null) {
            games.replace(gameID, new ListGameResult(gameID, game.whiteUsername(), username, game.gameName()));
            return true;
        }

        return false;
    }

    public int createGame(String name) throws DataAccessException {
        GameData game = new GameData(next++, null, null, name, new ChessGame());
        ListGameResult gameResult = new ListGameResult(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName());
        if (games.containsValue(gameResult)) {
            throw new DataAccessException("Game Already Exists");
        }
        games.put(game.gameID(), gameResult);
        return game.gameID();
    }
}
