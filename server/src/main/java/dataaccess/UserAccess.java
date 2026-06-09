package dataaccess;

import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;

import model.*;
import results.LoginRequest;

public class UserAccess implements UserDAO {

    final private ConcurrentHashMap<String, String> users = new ConcurrentHashMap<>();

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

