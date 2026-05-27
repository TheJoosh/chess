package dataaccess;

import chess.ChessGame;
import model.*;
import java.sql.Connection;

public interface GameDAO {

    void clear() throws DataAccessException;

    GameList listGames(Connection conn) throws DataAccessException;

    boolean joinGame(String username, ChessGame.TeamColor color, int gameID) throws DataAccessException;

    int createGame(String game) throws DataAccessException;

}
