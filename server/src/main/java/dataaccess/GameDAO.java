package dataaccess;

import model.*;

public interface GameDAO {

    void clear() throws DataAccessException;

    GameData createGame(GameData game) throws DataAccessException;

}
