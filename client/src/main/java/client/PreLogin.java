package client;

import server.ServerFacade;

import java.util.Arrays;
import java.util.Scanner;

import exception.ResponseException;
import results.*;
import model.*;

public class PreLogin {
    private final ServerFacade server;

    public static final String RESET = "\u001B[0m";
    public static final String PURPLE = "\u001B[35m";

    String url;
    boolean signedIn = false;
    String auth;

    public PreLogin (String url) throws ResponseException {
        server = new ServerFacade(url);
        this.url = url;
    }

    public void run() {
        System.out.println();
        System.out.println(PURPLE + "Log in or Register to play Chess.");
        System.out.println();
        System.out.print(help());
        System.out.println();

        @SuppressWarnings("resource")
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
                if (signedIn) {
                    try {
                        new PostLogin(auth, url).run();
                        return;
                    } catch (Throwable ex) {
                        System.out.printf("Unable to sign in: %s%n", ex.getMessage());
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
                case "login" -> login(params);
                case "register" -> register(params);
                case "clear" -> clear(params);
                case "quit" -> "Closing chess";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String login(String... params) throws ResponseException {
        if (params.length == 2) {
            try {
                LoginRequest request = new LoginRequest(params[0], params[1]);
                auth = server.login(request).authToken();
                signedIn = true;
                return String.format("Signed in as %s\n", params[0]);
            } catch (Exception e) {
                throw new ResponseException(ResponseException.Code.BadRequest, "Incorrect username or password\n");
            }
        }
        throw new ResponseException(ResponseException.Code.BadRequest, "Expected: <username> <password>\n");
    }

    public String register(String... params) throws ResponseException {
        if (params.length == 3) {
            try {
                UserData request = new UserData(params[0], params[1], params[2]);
                auth = server.register(request).authToken();
                signedIn = true;
                return String.format("Signed in as %s\n", params[0]);
            } catch (Exception e) {
                throw new ResponseException(ResponseException.Code.BadRequest, "Existing username or email\n");
            }
        }
        throw new ResponseException(ResponseException.Code.BadRequest, "Expected: <username> <password> <email>\n");
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
                - login <username> <password>
                - register <username> <password> <email>
                - help
                - quit
                """;
    }
}
