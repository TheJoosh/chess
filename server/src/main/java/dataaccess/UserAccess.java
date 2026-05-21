package dataaccess;

import java.util.HashMap;
import java.util.UUID;

import model.UserList;
import model.*;

public class UserAccess implements UserDAO {

    final private HashMap<String, UserData> users = new HashMap<>();

    public UserData getUser(int id) {
        return new UserData();
    }

    public UserList listUsers() {
        return new UserList(users.values());
    } 

    public void clear() {
        users.clear();
    }

    public AuthData register(UserData user) {
        String token = UUID.randomUUID().toString();
        users.put(token, user);
        AuthData auth = new AuthData(token, user.username());
        return auth;
    }
}

