package service;

import dataaccess.AuthAccess;
import dataaccess.DataAccessException;
import model.*;
import java.util.Collection;

public class AuthService {

    private final AuthAccess authAccess;

    public AuthService(AuthAccess authAccess) {
        this.authAccess = authAccess;
    }

    public void clear() throws DataAccessException {
        Collection<String> auth = authAccess.listAuth();
        if (!auth.isEmpty()) {
            authAccess.clear();
        }
    }

    public boolean authenticate(AuthData auth) throws DataAccessException {
        return authAccess.authenticate(auth);
    }

    public void removeAuth(AuthData auth) throws DataAccessException {
        authAccess.removeAuth(auth);
    }

    public String login(UserData user) throws DataAccessException {
        return authAccess.login(user);
    }
}
