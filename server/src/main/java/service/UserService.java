package service;

import dataaccess.DataAccessException;
import dataaccess.DataAccess;
import model.*;
import results.LoginRequest;
import java.util.Collection;

public class UserService {

    private final DataAccess userAccess;

    public UserService(DataAccess userAccess) {
        this.userAccess = userAccess;
    }

    public boolean getUser(UserData user) throws DataAccessException {
        return userAccess.getUser(user);
    }

    public UserList listUsers() throws DataAccessException {
        return userAccess.listUsers();
    }

    public void clear() throws DataAccessException {
        Collection<String> users = userAccess.listUsers();
        if (!users.isEmpty()) {
            userAccess.clear();
        }
    }

    public AuthData register(UserData user) throws DataAccessException {
        if (user == null || user.username() == null || user.email() == null || user.password() == null) {
            throw new DataAccessException("Invalid Input");
        }
        
        AuthData auth = userAccess.register(user);
        return auth;
    }

    public boolean verifyPassword(LoginRequest user) throws DataAccessException {
        return userAccess.verifyPassword(user);
    }
}
