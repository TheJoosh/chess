package dataaccess;

import java.util.HashMap;

import model.UserList;
import model.UserData;

public class UserAccess implements UserDAO {

    final private HashMap<Integer, UserData> users = new HashMap<>();

    public UserData getUser(int id) {
        return new UserData();
    }

    public UserList listUsers() {
        return new UserList(users.values());
    } 

    public void clear() {
        users.clear();
    }
}
