package dataaccess;

import java.util.HashMap;
import java.util.UUID;

import model.UserList;
import model.UserData;

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

    public UserData register(UserData user) {
        users.put(UUID.randomUUID().toString(), user);
        return user;
    }
}

