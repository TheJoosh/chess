package dataaccess;

import model.*;

public interface UserDAO {
    UserData addUser(UserData user) throws DataAccessException;

    UserData getUser(int id) throws DataAccessException;

    void deleteUser(Integer id) throws DataAccessException;

    void clear() throws DataAccessException;
}
