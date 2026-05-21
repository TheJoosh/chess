package service;

import dataaccess.GameAccess;
import dataaccess.DataAccessException;
import model.GameData;
import model.GameList;
import java.util.Collection;

public class GameService {

    private final GameAccess gameAccess;

    public GameService(GameAccess gameAccess) {
        this.gameAccess = gameAccess;
    }

    public void clear() throws DataAccessException {
        Collection<String> games = gameAccess.listGames();
        if (!games.isEmpty()) {
            gameAccess.clear();
        }
    }

    public GameData createGame(GameData game) throws DataAccessException {
        gameAccess.createGame(game);
        return game;
    }

    public GameList listGames() throws DataAccessException {
        return gameAccess.listGames();
    }

    public void joinGame(String game) throws DataAccessException {
        gameAccess.joinGame(game);
    }

}
