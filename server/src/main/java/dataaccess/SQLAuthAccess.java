package dataaccess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import model.AuthData;
import model.AuthList;
import results.LoginRequest;

import java.sql.Connection;

public class SQLAuthAccess implements AuthDAO{

    public void clear() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "TRUNCATE TABLE authData";
            SQLDataAccess.executeUpdate (conn, statement);
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
    }

    public void addAuth(AuthData authData) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "INSERT INTO authData (authToken, username) VALUES (?, ?)";
            SQLDataAccess.executeUpdate(conn, statement, authData.authToken(), authData.username());
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
    }

    public void removeAuth(String token) throws DataAccessException { 
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "DELETE FROM authData WHERE authToken=?";
            SQLDataAccess.executeUpdate(conn, statement, token);
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
    }
    
    public boolean authenticate(String token) throws DataAccessException {
        AuthList list = listAuth();
        return list.contains(token);
    }

    public AuthData login(LoginRequest user) throws DataAccessException {
        AuthData auth = new AuthData(UUID.randomUUID().toString(), user.username());
        addAuth(auth);
        return auth;
    }

    public AuthList listAuth() throws DataAccessException {
        var result = new AuthList();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, authToken FROM authData";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(readAuth(rs).authToken());
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return result;
    }

    public String getUsername(String auth) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken, username FROM authData";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    String verify = verifyAuth(rs, ps, auth);
                    if (verify != null) {
                        return verify;
                    }
                    throw new DataAccessException ("Error: Unauthorized");
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
    }

    private String verifyAuth (ResultSet rs, PreparedStatement ps, String auth) throws SQLException {
        while (rs.next()) {
            if (readAuth(rs).authToken().equals(auth)) {
                return readAuth(rs).username();
            }
        }
        return null;
    }

    private AuthData readAuth(ResultSet rs) throws SQLException {
        AuthData auth = new AuthData(rs.getString("authToken"), rs.getString("username"));
        return auth;
    }
}
