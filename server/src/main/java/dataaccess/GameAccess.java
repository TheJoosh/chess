package dataaccess;

import java.util.HashMap;
import java.util.UUID;

import com.google.gson.Gson;

import chess.ChessGame;
import model.GameList;
import model.GameData;

public class GameAccess implements GameDAO {

    final private HashMap<String, String> games = new HashMap<>();

    public GameList listGames() throws DataAccessException {
        return new GameList(games.values());
    } 

    public void clear() throws DataAccessException {
        games.clear();
    }

    public void joinGame(String gameName) throws DataAccessException {
        String game = games.get(gameName);
    }

    public GameData createGame(GameData game) throws DataAccessException {
        var serializer = new Gson();
        String gameString = serializer.toJson(game.game());
        games.put(game.gameName(), gameString);
        return game;
    }
}
