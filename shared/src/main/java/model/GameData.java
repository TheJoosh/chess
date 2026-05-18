package model;

import chess.ChessGame;

public class GameData {

    private final int gameID;
    private final String whiteUsername;
    private final String blackUsername;
    private final String gameName;
    private final ChessGame game;

    public GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {

        this.gameID = gameID;
        this.blackUsername = blackUsername;
        this.whiteUsername = whiteUsername;
        this.gameName = gameName;
        this.game = game;
    }

    //gets the game ID
    public int getGameID() {
        return gameID;
    }

    //gets the white user
    public String getWhite() {
        return whiteUsername;
    }

    //gets the black user
    public String getBlack() {
        return blackUsername;
    }

    //gets the game name
    public String getName() {
        return gameName;
    }

    //returns the game object
    public ChessGame getGame() {
        return game;
    }

    
}
