package model;

import java.util.Collection;
import java.util.ArrayList;

import com.google.gson.Gson;

public class UserList extends ArrayList<String> {
    public UserList() {

    }

    public UserList(Collection<String> data) {
        super(data);
    }

    public String toString() {
        return new Gson().toJson(this.toArray());
    }
}
