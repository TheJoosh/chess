package service;

import dataaccess.GameAccess;
import dataaccess.DataAccessException;
import model.GameList;
import model.GameData;
import model.ListGameResult;
import java.util.Collection;
import results.JoinRequest;

public class GameService {

    private final GameAccess gameAccess;

    public GameService(GameAccess gameAccess) {
        this.gameAccess = gameAccess;
    }

    public void clear() throws DataAccessException {
        Collection<ListGameResult> games = gameAccess.listGames();
        if (!games.isEmpty()) {
            gameAccess.clear();
        }
    }

    public int createGame(String game) throws DataAccessException {
        return gameAccess.createGame(game);
    }

    public GameList listGames() throws DataAccessException {
        return gameAccess.listGames();
    }

    public void joinGame(JoinRequest game) throws DataAccessException {
        gameAccess.joinGame(game);
    }

}
