package dataaccess;

import java.util.HashMap;
import java.util.UUID;

import model.*;

public class AuthAccess implements AuthDAO {

    final private HashMap<String, String> auth = new HashMap<>();

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

    public AuthData login(UserData user) {
        String token = UUID.randomUUID().toString();
        AuthData auth = new AuthData(token, user.username());
        addAuth(auth);
        return auth;
    }

}
