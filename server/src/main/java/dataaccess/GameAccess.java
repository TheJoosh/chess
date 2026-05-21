package dataaccess;

import java.util.HashMap;
import java.util.UUID;

import model.GameList;
import model.GameData;

public class GameAccess implements GameDAO {

    final private HashMap<String, GameData> games = new HashMap<>();

    public GameData getGames(int id) {
        return new GameData();
    }

    public GameList listGames() {
        return new GameList(games.values());
    } 

    public void clear() {
        games.clear();
    }

    public GameData createGame(GameData game) {
        games.put(UUID.randomUUID().toString(), game);
        return game;
    }
}
