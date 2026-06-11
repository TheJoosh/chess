package websocket.commands;

import java.util.Objects;

import chess.ChessGame;
import chess.ChessMove;

/**
 * Represents a command a user can send the server over a websocket
 * <p>
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class UserGameCommand {

    private final CommandType commandType;

    private final String authToken;
    private String username = null;
    private ChessGame.TeamColor team = null;
    private final Integer gameID;
    private ChessMove move = null;
    private boolean check;
    private boolean checkmate;

    public UserGameCommand(CommandType commandType, String authToken, Integer gameID) {
        this.commandType = commandType;
        this.authToken = authToken;
        this.gameID = gameID;
    }

    public enum CommandType {
        CONNECT,
        MAKE_MOVE,
        LEAVE,
        RESIGN
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setMove(ChessMove move) {
        this.move = move;
    }

    public ChessMove getMove() {
        return move;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public void setMate(boolean checkmate) {
        this.checkmate = checkmate;
    }

    public boolean inCheck() {
        return check;
    }

    public boolean inMate() {
        return checkmate;
    }

    public void setTeam(ChessGame.TeamColor team) {
        this.team = team;
    }

    public ChessGame.TeamColor getTeam() {
        return team;
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public String getAuthToken() {
        return authToken;
    }

    public Integer getGameID() {
        return gameID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserGameCommand that)) {
            return false;
        }
        return getCommandType() == that.getCommandType() &&
                Objects.equals(getAuthToken(), that.getAuthToken()) &&
                Objects.equals(getGameID(), that.getGameID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCommandType(), getAuthToken(), getGameID());
    }
}
