package dataaccess;

import model.AuthData;
import model.UserData;
import results.LoginRequest;

public class SQLUserAccess implements UserDAO{

    public boolean getUser(UserData user) throws DataAccessException {
        return false;
    }
    
    public boolean verifyPassword(LoginRequest user) {
        return false;
    }

    public AuthData register (UserData user) throws DataAccessException {
        return new AuthData();
    }

    public void clear() throws DataAccessException {

    }

}
