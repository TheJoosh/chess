package dataaccess;

import model.*;

public interface AuthDAO {

    void clear() throws DataAccessException;

    void removeAuth(String token) throws DataAccessException;
    
    boolean authenticate(String token) throws DataAccessException;

    AuthData login(UserData user) throws DataAccessException;

    AuthList listAuth() throws DataAccessException;

    String getUsername(String auth) throws DataAccessException;

}
