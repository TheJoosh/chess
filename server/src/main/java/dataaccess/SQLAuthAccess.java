package dataaccess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.google.gson.Gson;

import chess.ChessGame;
import model.AuthData;
import model.AuthList;
import results.LoginRequest;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;
import java.sql.Connection;

public class SQLAuthAccess implements AuthDAO{

    public void clear() throws DataAccessException {
        var statement = "TRUNCATE TABLE authData";
        executeUpdate (statement);
    }

    public void addAuth(AuthData authData) throws DataAccessException {
        var statement = "INSERT INTO authData (token, username, json) VALUES (?, ?, ?)";
        String json = new Gson().toJson(authData);
        executeUpdate(statement, authData.authToken(), authData.username(), json);
    }

    public void removeAuth(String token) throws DataAccessException { 
        var statement = "DELETE FROM pet WHERE token=?";
        executeUpdate(statement, token);
    }
    
    public boolean authenticate(String token) throws DataAccessException {
        return false;
    }

    public AuthData login(LoginRequest user) throws DataAccessException {
        return new AuthData();
    }

    public AuthList listAuth() throws DataAccessException {
        return new AuthList();
    }

    public String getUsername(String auth) throws DataAccessException {
        return "";
    }

    
    public int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                    else if (param instanceof ChessGame.TeamColor p) ps.setString(i + 1, p.toString());
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

}
