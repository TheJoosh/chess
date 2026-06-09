package dataaccess;

import java.util.concurrent.ConcurrentHashMap;

import model.GameList;
import model.GameData;
import chess.ChessGame;
import java.sql.Connection;

public class GameAccess implements GameDAO {

    final private ConcurrentHashMap<Integer, GameData> games = new ConcurrentHashMap<>();
    private int next = 1;

    public GameList listGames(Connection conn) throws DataAccessException {
        GameList newList = new GameList();
        newList.putAll(games);
        return newList;
    } 

    public void clear() throws DataAccessException {
        games.clear();
    }

    public GameData joinGame(String username, ChessGame.TeamColor color, int gameID) throws DataAccessException {
        GameData game = games.get(gameID);

        if (color == ChessGame.TeamColor.WHITE && game.whiteUsername() == null) {
            games.replace(gameID, new GameData(gameID, username, game.blackUsername(), game.gameName(), game.game()));
            return game;

        } else if (color == ChessGame.TeamColor.BLACK && game.blackUsername() == null) {
            games.replace(gameID, new GameData(gameID, game.whiteUsername(), username, game.gameName(), game.game()));
            return game;
        }

        return null;
    }

    public int createGame(String name) throws DataAccessException {
        GameData game = new GameData(next++, null, null, name, new ChessGame());
        if (games.containsValue(game)) {
            throw new DataAccessException("Game Already Exists");
        }
        games.put(game.gameID(), game);
        return game.gameID();
    }
}
