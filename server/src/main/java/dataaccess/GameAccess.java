package dataaccess;

import java.util.HashMap;
import java.util.UUID;

import model.GameList;
import model.GameData;

public class GameAccess implements GameDAO {

    final private HashMap<String, GameData> games = new HashMap<>();

    public GameList listGames() throws DataAccessException {
        return new GameList(games.values());
    } 

    public void clear() throws DataAccessException {
        games.clear();
    }

    public void joinGame(GameData gameData) throws DataAccessException {
        GameData game = games.get(gameData.gameName());
    }

    public GameData createGame(GameData game) throws DataAccessException {
        games.put(UUID.randomUUID().toString(), game);
        return game;
    }
}
