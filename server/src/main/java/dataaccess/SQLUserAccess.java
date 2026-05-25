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

    public UserList listUsers() {
        return new UserList();
    } 

    public boolean getUser(UserData user) throws DataAccessException {
        return false;
    }
    
    public boolean verifyPassword(LoginRequest user) {
        return false;
    }

    public AuthData register (UserData user) throws DataAccessException {
        var statement = "INSERT INTO users (username, email, hashedpassword, json) VALUES (?, ?, ?, ?)";
        String json = new Gson().toJson(user);
        String hashWord = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        executeUpdate(statement, user.username(), user.email(), hashWord, json);
        return new AuthData(UUID.randomUUID().toString(), user.username());
    }

    public void clear() throws DataAccessException {
        var statement = "TRUNCATE TABLE users";
        executeUpdate (statement);
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
