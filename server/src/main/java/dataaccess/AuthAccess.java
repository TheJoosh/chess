package dataaccess;

import java.util.HashMap;

import model.*;

public class AuthAccess implements AuthDAO {

    final private HashMap<Integer, AuthData> auth = new HashMap<>();

    public void clear() {
        auth.clear();
    }

    public AuthList listAuth() {
        return new AuthList(auth.values());
    }

}
