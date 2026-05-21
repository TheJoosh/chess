package results;

import java.util.List;
import model.ListGameResult;

public class ListGamesResults {
    private List<ListGameResult> games;

    public ListGamesResults(List<ListGameResult> games) {
        this.games = games;
    }

    public List<ListGameResult> getGames() {
        return games;
    }
}
