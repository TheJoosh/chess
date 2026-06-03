package client;

import server.ServerFacade;

import java.util.List;
import java.util.Arrays;
import java.util.Scanner;

import com.google.gson.Gson;

import exception.ResponseException;
import results.*;
import model.*;

public class PostLogin {
    private final ServerFacade server;

    public static final String RESET = "\u001B[0m";
    public static final String PURPLE = "\u001B[35m";
    public static final String RED = "\u001B[31m";

    private String url;
    private String auth;
    boolean signedIn = true;

    public PostLogin (String auth, String url) throws ResponseException {
        server = new ServerFacade(url);
        this.url = url;
        this.auth = auth;
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
                    try {
                        new PreLogin(url).run();
                        return;
                    } catch (Throwable ex) {
                        System.out.printf("Unable to sign out: %s%n", ex.getMessage());
                    }
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
                case "list" -> listGames();
                case "join" -> joinGame(params);
                case "observe" -> observeGame(params);
                case "clear" -> clear(params);
                case "logout" -> logout();
                case "quit" -> "Closing chess";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String logout() throws ResponseException {
        try {
            System.out.print("Logout\n");
            server.logout(auth, null);
            signedIn = false;
        } catch (Exception e) {
            throw new ResponseException(ResponseException.Code.BadRequest, "Failed to log out\n");
        }
        return "Signed out";
    }

    public String createGame(String... params) throws ResponseException {
        if (params.length == 1) {
            try {
                CreateRequest request = new CreateRequest(auth, params[0]);
                server.createGame(auth, request);
                return String.format("Created game %s\n", params[0]);
            } catch (Exception e) {
                throw new ResponseException(ResponseException.Code.Unauthorized, "Unauthorized\n");
            }
        }
        throw new ResponseException(ResponseException.Code.BadRequest, "Expected: <game name>\n");
    }

    public String listGames() throws ResponseException {
        try {
            List<ListGameResult> result = server.listGames(auth, null).getGames();
            System.out.print("listGames\nresult: " + result.toString() + "\n\n");
            System.out.print(PURPLE + "Active Games:\n");

            int i = 1;
            for (ListGameResult item : result) {
                System.out.print("   " + i + ". " + item.gameName() + " - White: ");
                if (item.whiteUsername() == null) {
                    System.out.print(RED + "None" + PURPLE + ", Black: ");
                } else {
                    System.out.print(item.whiteUsername() + ", Black: ");
                }

                if (item.blackUsername() == null) {
                    System.out.print(RED + "None\n" + PURPLE);
                } else {
                    System.out.print(item.blackUsername() + "\n");
                }
                i++;
            }

            System.out.print(RESET);

            return "";
        } catch (Exception e) {
            throw new ResponseException(ResponseException.Code.Unauthorized, "Unauthorized\n");
        }
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
                signedIn = false;
                return "Data cleared";
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