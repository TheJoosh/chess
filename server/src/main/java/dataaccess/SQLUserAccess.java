package dataaccess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.mindrot.jbcrypt.BCrypt;

import model.AuthData;
import model.UserData;
import model.UserList;
import results.LoginRequest;


import java.sql.Connection;

public class SQLUserAccess implements UserDAO{

    public UserList listUsers() throws DataAccessException {
        var result = new UserList();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, email, password FROM users";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(readUsers(rs).username());
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return result;
    } 

    public boolean getUser(UserData user) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, email, password FROM users";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    boolean found = false;

                    while (rs.next()) {
                        found = readUsers(rs).username().equals(user.username());
                    }
                    return found;
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
    }
    
    public boolean verifyPassword(LoginRequest user) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password FROM users";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ResultSet result = ps.executeQuery();
                boolean found = false;
                while (result.next()) {
                    if (result.getString("username").equals(user.username())) {
                        found = BCrypt.checkpw(user.password(), result.getString("password"));
                    }
                }
                return found;
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
    }

    public AuthData register (UserData user) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
            String hashWord = BCrypt.hashpw(user.password(), BCrypt.gensalt());
            SQLDataAccess.executeUpdate(conn, statement, user.username(), user.email(), hashWord);
            return new AuthData(UUID.randomUUID().toString(), user.username());
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
    }

    public void clear() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "TRUNCATE TABLE users";
            SQLDataAccess.executeUpdate (conn, statement);
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
    }

    private UserData readUsers(ResultSet rs) throws SQLException {
        UserData user = new UserData(rs.getString("username"), rs.getString("password"), rs.getString("email"));
        return user;
    }
}
