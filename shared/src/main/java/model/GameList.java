package model;

import java.util.Collection;
import java.util.ArrayList;

import com.google.gson.Gson;

public class GameList extends ArrayList<ListGameResult> {
    public GameList() {

    }

    public GameList(Collection<ListGameResult> data) {
        super(data);
    }

    public String toString() {
        return new Gson().toJson(this.toArray());
    }
}
