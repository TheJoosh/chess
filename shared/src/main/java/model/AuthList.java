package model;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collection;

public class AuthList extends ArrayList<AuthData> {
    public AuthList() {

    }

    public AuthList(Collection<AuthData> auth) {
        super(auth);
    }

    public String toString() {
        return new Gson().toJson(this.toArray());
    }
}
