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

    public boolean authenticate(AuthData authData) {
        return auth.containsValue(authData.username());
    }

    public void removeAuth(AuthData authData) {
        auth.remove(authData.authToken(), authData.username());
    }

    public void addAuth(AuthData authData) {
        auth.put(authData.authToken(), authData.username());
    }

    public String login(UserData user) {
        String token = UUID.randomUUID().toString();
        auth.put(token, user.username());
        return token;
    }

}
