package dataaccess;

import model.*;

public interface UserDAO {

    UserData getUser(int id) throws DataAccessException;

    AuthData register (UserData user) throws DataAccessException;

    void clear() throws DataAccessException;
}
