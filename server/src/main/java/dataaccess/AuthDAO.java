package dataaccess;

import model.*;

public interface AuthDAO {

    void clear() throws DataAccessException;

    void removeAuth(AuthData auth) throws DataAccessException;

    AuthList listAuth() throws DataAccessException;

}
