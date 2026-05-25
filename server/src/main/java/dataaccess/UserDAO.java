package dataaccess;

import model.*;
import results.LoginRequest;

public interface UserDAO {

    public boolean getUser(UserData user) throws DataAccessException;

    public UserList listUsers() throws DataAccessException;
    
    public boolean verifyPassword(LoginRequest user) throws DataAccessException;

    public AuthData register (UserData user) throws DataAccessException;

    public void clear() throws DataAccessException;
}
