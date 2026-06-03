package client;

import java.util.Scanner;

import exception.ResponseException;
import results.*;
import server.ServerFacade;
import model.*;

public class Gameplay {
    private final ServerFacade server;

    public static final String RESET = "\u001B[0m";
    public static final String PURPLE = "\u001B[35m";

    boolean inGame = false;
    String url;
    String auth;

    public Gameplay (String auth, String url) throws ResponseException {
        server = new ServerFacade(url);
        this.url = url;
        this.auth = auth;
    }

    public void run() {
        
    }

    public void draw() {
        
    }
}
