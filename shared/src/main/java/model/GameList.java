package model;

import java.util.HashMap;

import com.google.gson.Gson;

public class GameList extends HashMap<Integer, GameData> {
    public GameList() {

    }

    public GameList(HashMap<Integer, GameData> data) {
        super(data);
    }

    public String toString() {
        return new Gson().toJson(this.values().toArray());
    }
}
