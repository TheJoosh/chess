package dataaccess;

import model.UserData;

public class UserAccess implements UserDAO {

    public UserData getUser(int id) {
        return new UserData();
    }

    public void clear() {}
}
