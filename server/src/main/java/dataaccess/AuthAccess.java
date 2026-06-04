package dataaccess;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import java.util.UUID;

import model.*;
import results.LoginRequest;

public class AuthAccess implements AuthDAO {

    final private ConcurrentHashMap<String, String> auth = new ConcurrentHashMap<String, String>();

    public void clear() {
        auth.clear();
    }

    public AuthList listAuth() {
        return new AuthList(auth.values());
    }

    public boolean authenticate(String token) {
        return auth.containsKey(token);
    }

    public void removeAuth(String token) {
        auth.remove(token);
    }

    public void addAuth(AuthData authData) {
        auth.put(authData.authToken(), authData.username());
    }

    public AuthData login(LoginRequest user) throws DataAccessException {
        String token = UUID.randomUUID().toString();
        AuthData auth = new AuthData(token, user.username());
        addAuth(auth);
        return auth;
    }

    public String getUsername(String authToken) throws DataAccessException {
        if (!auth.containsKey(authToken)) {
            throw new DataAccessException("Invalid Token");
        }
        return auth.get(authToken);
    }
}
