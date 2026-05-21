package service;

import dataaccess.UserAccess;
import exception.*;
import model.*;
import java.util.Collection;

public class UserService {

    private final UserAccess userAccess;

    public UserService(UserAccess userAccess) {
        this.userAccess = userAccess;
    }

    public void clear() throws ResponseException {
        Collection<UserData> users = userAccess.listUsers();
        if (!users.isEmpty()) {
            userAccess.clear();
        }
    }

    public UserData register(UserData user) {
        return user;
    }

}
