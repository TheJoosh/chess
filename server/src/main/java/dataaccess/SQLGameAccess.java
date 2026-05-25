package dataaccess;

import chess.ChessGame;
import model.GameList;

public class SQLGameAccess implements GameDAO {

    public void clear() throws DataAccessException {

    }

    public GameList listGames() throws DataAccessException {
        return new GameList();
    }

    public boolean joinGame(String username, ChessGame.TeamColor color, int gameID) throws DataAccessException {
        return false;
    }

    public int createGame(String game) throws DataAccessException {
        return 0;
    }

}
