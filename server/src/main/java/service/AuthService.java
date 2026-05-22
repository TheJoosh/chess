package service;

import dataaccess.AuthAccess;
import dataaccess.DataAccessException;
import model.*;
import java.util.Collection;
import results.LoginRequest;

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
        if (!authenticate(auth)) {
            throw new DataAccessException("Invalid Token");
        }
        authAccess.removeAuth(auth);
    }

    public void addAuth(AuthData auth) throws DataAccessException {
        if (auth == null || auth.authToken() == null || auth.username() == null) {
            throw new DataAccessException("Invalid Input");
        }
        authAccess.addAuth(auth);
    }

    public AuthList listAuth() throws DataAccessException {
        return authAccess.listAuth();
    }

    public AuthData login(LoginRequest user) throws DataAccessException {
        if (user == null || user.username() == null || user.password() == null) {
            throw new DataAccessException("Invalid Input");
        }
        return authAccess.login(user);
    }

    public String getUsername(String authToken) throws DataAccessException {
        if (authToken == null) {
            throw new DataAccessException("Invalid Token");
        }
        return authAccess.getUsername(authToken);
    }
}
