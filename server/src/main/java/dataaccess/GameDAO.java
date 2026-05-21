package dataaccess;

import model.*;
import results.JoinRequest;

public interface GameDAO {

    void clear() throws DataAccessException;

    GameList listGames() throws DataAccessException;

    void joinGame(JoinRequest gameName) throws DataAccessException;

    int createGame(String game) throws DataAccessException;

}
