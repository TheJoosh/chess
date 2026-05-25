package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.AuthList;
import model.GameList;
import model.UserData;
import model.UserList;
import results.LoginRequest;

public interface DataAccess {

    public void clear() throws DataAccessException;

    public boolean getUser(UserData user) throws DataAccessException;

    public UserList listUsers() throws DataAccessException;

    public boolean verifyPassword(LoginRequest user) throws DataAccessException;

    public AuthData register (UserData user) throws DataAccessException;

    public void addAuth(AuthData authData) throws DataAccessException;

    public void removeAuth(String token) throws DataAccessException;

    public boolean authenticate(String token) throws DataAccessException;

    public AuthData login(LoginRequest user) throws DataAccessException;

    public AuthList listAuth() throws DataAccessException;

    public String getUsername(String auth) throws DataAccessException;

    public GameList listGames() throws DataAccessException;

    public boolean joinGame(String username, ChessGame.TeamColor color, int gameID) throws DataAccessException;

    public int createGame(String game) throws DataAccessException;
}
