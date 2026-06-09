package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.GameList;
import model.GameData;
import results.ListGamesResults;
import results.Result;

import chess.ChessGame;

public class GameService {

    private final DataAccess gameAccess;

    public GameService(DataAccess gameAccess) {
        this.gameAccess = gameAccess;
    }

    public void clear() throws DataAccessException {
        ListGamesResults games = new ListGamesResults(gameAccess.listGames());
        if (!games.getGames().isEmpty()) {
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

    public Result joinGame(String username, ChessGame.TeamColor color, int gameID) throws DataAccessException {
        if ((color != ChessGame.TeamColor.BLACK && color != ChessGame.TeamColor.WHITE) || username == null) {
            return new Result("Invalid Input", null);
        }
        
        GameData success = gameAccess.joinGame(username, color, gameID);
        if (success != null) {
            return new Result(null, success);
        }
        return new Result("Color Already Taken", null);
    }

}
