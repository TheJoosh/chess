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

    public boolean authenticate(String auth) throws DataAccessException {
        return authAccess.authenticate(auth);
    }

    public void removeAuth(String auth) throws DataAccessException {
        authAccess.removeAuth(auth);
    }

    public void addAuth(AuthData auth) throws DataAccessException {
        authAccess.addAuth(auth);
    }

    public AuthData login(UserData user) throws DataAccessException {
        return authAccess.login(user);
    }
}
