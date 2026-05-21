package service;

import dataaccess.AuthAccess;
import exception.*;
import model.*;
import java.util.Collection;

public class AuthService {

    private final AuthAccess authAccess;

    public AuthService(AuthAccess authAccess) {
        this.authAccess = authAccess;
    }

    public void clear() throws ResponseException {
        Collection<String> auth = authAccess.listAuth();
        if (!auth.isEmpty()) {
            authAccess.clear();
        }
    }

    public boolean authenticate(AuthData auth) {
        return authAccess.authenticate(auth);
    }

    public void removeAuth(AuthData auth) {
        authAccess.removeAuth(auth);
    }
}
