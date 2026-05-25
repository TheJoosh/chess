package dataaccess;

import model.AuthData;
import model.AuthList;
import results.LoginRequest;

public class SQLAuthAccess implements AuthDAO{

    public void clear() throws DataAccessException {

    }

    public void removeAuth(String token) throws DataAccessException { 

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

}
