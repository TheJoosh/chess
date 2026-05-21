package dataaccess;

import model.*;
import results.LoginRequest;

public interface AuthDAO {

    void clear() throws DataAccessException;

    void removeAuth(String token) throws DataAccessException;
    
    boolean authenticate(String token) throws DataAccessException;

    AuthData login(LoginRequest user) throws DataAccessException;

    AuthList listAuth() throws DataAccessException;

    String getUsername(String auth) throws DataAccessException;

}
