package dataaccess;

import model.*;

public interface GameDAO {

    void clear() throws DataAccessException;

    GameList listGames() throws DataAccessException;

    void joinGame(GameData game) throws DataAccessException;

    GameData createGame(GameData game) throws DataAccessException;

}
