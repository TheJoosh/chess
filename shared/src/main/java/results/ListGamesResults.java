package results;

import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import model.GameData;

public class ListGamesResults {
    private List<GameData> games;

    public ListGamesResults(HashMap<Integer, GameData> games) {
        this.games = new ArrayList<GameData>();
        for (GameData item : games.values()) {
            this.games.add(item);
        }
    }

    public List<GameData> getGames() {
        return games;
    }
}
