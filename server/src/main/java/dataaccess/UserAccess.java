package dataaccess;

import java.util.HashMap;
import java.util.UUID;

import model.*;
import results.LoginRequest;

public class UserAccess implements UserDAO {

    final private HashMap<String, String> users = new HashMap<>();

    public UserData getUser(int id) {
        return new UserData();
    }

    public UserList listUsers() {
        return new UserList(users.keySet());
    } 

    public void clear() {
        users.clear();
    }

    public boolean getUser(UserData user) {
        return users.containsKey(user.username());
    }

    public boolean verifyPassword(LoginRequest user) {
        if (users.containsKey(user.username())) {
            return user.password().equals(users.get(user.username()));
        }
        return false;
    }

    public AuthData register(UserData user) {
        String token = UUID.randomUUID().toString();
        users.put(user.username(), user.password());
        AuthData auth = new AuthData(token, user.username());
        return auth;
    }
}

