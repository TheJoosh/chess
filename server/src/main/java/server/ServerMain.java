package server;

import dataaccess.*;
import dataaccess.DataAccessException;

public class ServerMain {
    public static void main(String[] args) throws DataAccessException {
        DataAccess dataAccess = new SQLDataAccess(new SQLGameAccess(), new SQLUserAccess(), new SQLAuthAccess());

        Server server = new Server();
        server.run(8080);

        System.out.println("♕ 240 Chess Server");
    }
}
