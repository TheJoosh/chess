package service;

import dataaccess.DataAccessException;
import dataaccess.UserAccess;
import model.*;
import java.util.Collection;

public class UserService {

    private final UserAccess userAccess;

    public UserService(UserAccess userAccess) {
        this.userAccess = userAccess;
    }

    public void clear() throws DataAccessException {
        Collection<UserData> users = userAccess.listUsers();
        if (!users.isEmpty()) {
            userAccess.clear();
        }
    }

    public AuthData register(UserData user) throws DataAccessException {
        AuthData auth = userAccess.register(user);
        return auth;
    }
}
