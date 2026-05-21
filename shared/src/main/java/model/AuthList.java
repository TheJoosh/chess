package model;

import java.util.Collection;
import java.util.ArrayList;

import com.google.gson.Gson;

public class AuthList extends ArrayList<String> {
    public AuthList() {

    }

    public AuthList(Collection<String> data) {
        super(data);
    }

    public String toString() {
        return new Gson().toJson(this.toArray());
    }
}
