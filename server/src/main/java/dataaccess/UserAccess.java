package dataaccess;

import java.util.HashMap;
import java.util.UUID;

import model.UserList;
import model.*;

public class UserAccess implements UserDAO {

    final private HashMap<String, String> users = new HashMap<>();

    public UserData getUser(int id) {
        return new UserData();
    }

    public UserList listUsers() {
        return new UserList(users.values());
    } 

    public void clear() {
        users.clear();
    }

    public boolean getUser(UserData user) {
        return users.containsValue(user.username()) || users.containsKey(user.email());
    }

    public AuthData register(UserData user) {
        String token = UUID.randomUUID().toString();
        users.put(user.email(), user.username());
        AuthData auth = new AuthData(token, user.username());
        return auth;
    }
}

