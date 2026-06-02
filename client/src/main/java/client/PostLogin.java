package client;

import server.ServerFacade;

import java.util.Arrays;
import java.util.Scanner;

import exception.ResponseException;
import results.*;
import model.*;

public class PostLogin {
    private final ServerFacade server;

    public static final String RESET = "\u001B[0m";
    public static final String PURPLE = "\u001B[35m";

    String url;
    boolean signedIn = true;

    public PostLogin (String url) throws ResponseException {
        server = new ServerFacade(url);
        this.url = url;
    }

    public void run() {

        System.out.print(PURPLE + help());
        System.out.println();

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("Closing chess")) {
            System.out.print(RESET);
            String line = scanner.nextLine();
            System.out.println();

            try {
                result = eval(line);
                System.out.print(PURPLE + result + RESET);
                System.out.println();
                if (!signedIn) {
                    return;
                }
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg + "\n\n");
            }
        }
        System.out.println();
    }

    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "create" -> createGame(params);
                case "list" -> listGames(params);
                case "join" -> joinGame(params);
                case "observe" -> observeGame(params);
                case "clear" -> clear(params);
                case "quit" -> "Closing chess";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String createGame(String... params) {
        return "Game created\n";
    }

    public String listGames(String... params) {
        return "Games listed\n";
    }

    public String joinGame(String... params) {
        return "Game joined\n";
    }

    public String observeGame(String... params) {
        return "Game observed\n";
    }

    public String clear(String... params) throws ResponseException {
        if (params.length == 0) {
            try {
                server.clear();
                return "Data cleared\n";
            } catch (Exception e) {
                throw new ResponseException(ResponseException.Code.BadRequest, "Clear unsuccessful\n");
            }
        }

        throw new ResponseException(ResponseException.Code.BadRequest, "Bad Input\n");
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