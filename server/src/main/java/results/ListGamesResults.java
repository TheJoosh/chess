package results;

import java.util.List;
import java.util.HashMap;
import model.ListGameResult;

public class ListGamesResults {
    private List<ListGameResult> games;

    public ListGamesResults(HashMap<Integer, ListGameResult> games) {
        for (ListGameResult item : games.values()) {
            this.games.add(item);
        }
    }

    public List<ListGameResult> getGames() {
        return games;
    }
}
