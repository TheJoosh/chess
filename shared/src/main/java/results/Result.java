package results;

import model.GameData;

public class Result {
    private String message;
    private GameData game;

    public Result(String message, GameData game) {
        this.message = message;
        this.game = game;
    }

    public String getMessage() {
        return message;
    }

    public GameData getGame() {
        return game;
    }
}

