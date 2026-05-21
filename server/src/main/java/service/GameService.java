package service;

import dataaccess.GameAccess;
import exception.ResponseException;
import model.GameData;
import java.util.Collection;

public class GameService {

    private final GameAccess gameAccess;

    public GameService(GameAccess gameAccess) {
        this.gameAccess = gameAccess;
    }

    public void clear() throws ResponseException {
        Collection<GameData> games = gameAccess.listGames();
        if (!games.isEmpty()) {
            gameAccess.clear();
        }
    }
}
