package dataaccess;

import model.*;

public interface AuthDAO {

    void clear() throws DataAccessException;

    AuthList listAuth() throws DataAccessException;

}
