package dataaccess;

import java.util.HashMap;

import results.JoinRequest;
import model.GameList;
import model.GameData;
import model.ListGameResult;
import chess.ChessGame;

public class GameAccess implements GameDAO {

    final private HashMap<Integer, ListGameResult> games = new HashMap<>();
    private int next = 1;

    public GameList listGames() throws DataAccessException {
        return new GameList(games.values());
    } 

    public void clear() throws DataAccessException {
        games.clear();
    }

    public boolean joinGame(JoinRequest gameRequest, String username) throws DataAccessException {
        ListGameResult game = games.get(gameRequest.gameID());

        if (gameRequest.color() == "WHITE" && game.whiteUsername() == null) {
            games.replace(gameRequest.gameID(), new ListGameResult(game.gameID(), username, game.blackUsername(), game.gameName()));
            return true;

        } else if (gameRequest.color() == "BLACK" && game.blackUsername() == null) {
            games.replace(gameRequest.gameID(), new ListGameResult(game.gameID(), game.whiteUsername(), username, game.gameName()));
            return true;
        }
        
        return false;
    }

    public int createGame(String name) throws DataAccessException {
        //var serializer = new Gson();
        GameData game = new GameData(next++, null, null, name, new ChessGame());
        ListGameResult gameResult = new ListGameResult(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName());
        //String gameString = serializer.toJson(game.game());
        games.put(game.gameID(), gameResult);
        return game.gameID();
    }
}
