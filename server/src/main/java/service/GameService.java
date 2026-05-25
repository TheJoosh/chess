package service;

import dataaccess.GameAccess;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.GameList;
import model.ListGameResult;
import java.util.Collection;

import chess.ChessGame;

public class GameService {

    private final DataAccess gameAccess;

    public GameService(DataAccess gameAccess) {
        this.gameAccess = gameAccess;
    }

    public void clear() throws DataAccessException {
        Collection<ListGameResult> games = gameAccess.listGames();
        if (!games.isEmpty()) {
            gameAccess.clear();
        }
    }

    public int createGame(String game) throws DataAccessException {
        if (game == null) {
            throw new DataAccessException("Invalid Name");
        }
        return gameAccess.createGame(game);
    }

    public GameList listGames() throws DataAccessException {
        return gameAccess.listGames();
    }

    public boolean joinGame(String username, ChessGame.TeamColor color, int gameID) throws DataAccessException {
        if ((color != ChessGame.TeamColor.BLACK && color != ChessGame.TeamColor.WHITE) || username == null) {
            throw new DataAccessException("Invalid Input");
        }
        return gameAccess.joinGame(username, color, gameID);
    }

}
