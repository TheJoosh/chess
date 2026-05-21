package dataaccess;

import java.util.HashMap;

import com.google.gson.Gson;

import results.JoinRequest;
import model.GameList;
import model.GameData;
import chess.ChessGame;

public class GameAccess implements GameDAO {

    final private HashMap<Integer, String> games = new HashMap<>();
    private int next = 1;

    public GameList listGames() throws DataAccessException {
        return new GameList(games.values());
    } 

    public void clear() throws DataAccessException {
        games.clear();
    }

    public void joinGame(JoinRequest gameRequest) throws DataAccessException {
        String game = games.get(gameRequest.gameID());
    }

    public int createGame(String name) throws DataAccessException {
        var serializer = new Gson();
        GameData game = new GameData(next++, null, null, name, new ChessGame());
        String gameString = serializer.toJson(game.game());
        games.put(game.gameID(), gameString);
        return game.gameID();
    }
}
