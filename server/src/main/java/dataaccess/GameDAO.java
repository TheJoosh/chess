package dataaccess;

import chess.ChessGame;
import model.*;

public interface GameDAO {

    void clear() throws DataAccessException;

    GameList listGames() throws DataAccessException;

    boolean joinGame(String username, ChessGame.TeamColor color, int gameID) throws DataAccessException;

    int createGame(String game) throws DataAccessException;

}
