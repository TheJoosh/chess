package dataaccess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.mindrot.jbcrypt.BCrypt;

import com.google.gson.Gson;

import chess.ChessGame;
import model.AuthData;
import model.UserData;
import model.UserList;
import results.LoginRequest;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;
import java.sql.Connection;

public class SQLUserAccess implements UserDAO{

    public UserList listUsers() throws DataAccessException {
        var result = new UserList();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, json FROM users";
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
            var statement = "SELECT username, password, json FROM users";
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
            var statement = "SELECT username, password FROM users WHERE username = ?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ResultSet result = ps.executeQuery(user.username());
                if (result.next()) {
                    return result.getString("password") == BCrypt.hashpw(user.password(), BCrypt.gensalt());
                }
                throw new DataAccessException("Invalid Username");
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
    }

    public AuthData register (UserData user) throws DataAccessException {
        var statement = "INSERT INTO users (username, email, password, json) VALUES (?, ?, ?, ?)";
        String json = new Gson().toJson(user);
        String hashWord = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        executeUpdate(statement, user.username(), user.email(), hashWord, json);
        return new AuthData(UUID.randomUUID().toString(), user.username());
    }

    public void clear() throws DataAccessException {
        var statement = "TRUNCATE TABLE users";
        executeUpdate (statement);
    }

    private UserData readUsers(ResultSet rs) throws SQLException {
        var json = rs.getString("json");
        UserData user = new Gson().fromJson(json, UserData.class);
        return user;
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
