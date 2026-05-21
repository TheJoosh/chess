package dataaccess;

import model.*;

public interface UserDAO {

    boolean getUser(UserData user) throws DataAccessException;

    AuthData register (UserData user) throws DataAccessException;

    void clear() throws DataAccessException;
}
