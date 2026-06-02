package client;

import server.ServerFacade;

import java.util.Arrays;
import java.util.Scanner;

import exception.ResponseException;
import results.*;
import model.*;

public class PostLogin {
    private final ServerFacade server;

    String url;
    Scanner scanner;

    public PostLogin (Scanner scanner, String url) throws ResponseException {
        server = new ServerFacade(url);
        this.url = url;
        this.scanner = scanner;
    }

    public void run() {

        System.out.print(help());
        System.out.println();


    }

    public String help() {
        return """
                - create <game name> 
                - list
                - join <id> [WHITE|BLACK]
                - observe <id>
                - help
                - logout
                - quit
                """;
    }
}