package dataaccess;

import java.util.HashMap;

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

}
