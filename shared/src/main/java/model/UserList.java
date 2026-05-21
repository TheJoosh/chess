package model;

import java.util.Collection;
import java.util.ArrayList;

import com.google.gson.Gson;

public class UserList extends ArrayList<UserData> {
    public UserList() {

    }

    public UserList(Collection<UserData> data) {
        super(data);
    }

    public String toString() {
        return new Gson().toJson(this.toArray());
    }
}
