package service;

import dataaccess.DataAccessException;
import dataaccess.UserAccess;
import model.*;
import results.LoginRequest;
import java.util.Collection;

public class UserService {

    private final UserAccess userAccess;

    public UserService(UserAccess userAccess) {
        this.userAccess = userAccess;
    }

    public boolean getUser(UserData user) {
        return userAccess.getUser(user);
    }

    public void clear() throws DataAccessException {
        Collection<String> users = userAccess.listUsers();
        if (!users.isEmpty()) {
            userAccess.clear();
        }
    }

    public AuthData register(UserData user) throws DataAccessException {
        AuthData auth = userAccess.register(user);
        return auth;
    }

    public boolean verifyPassword(LoginRequest user) {
        return userAccess.verifyPassword(user);
    }
}
