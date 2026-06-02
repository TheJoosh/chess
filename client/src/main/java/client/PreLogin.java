package client;

import server.ServerFacade;

import java.util.Arrays;
import java.util.Scanner;

import exception.ResponseException;
import results.*;
import model.*;

public class PreLogin {
    private final ServerFacade server;

    String url;

    public PreLogin (String url) throws ResponseException {
        server = new ServerFacade(url);
        this.url = url;
    }

    public void run() {
        System.out.println("Log in or Register to play Chess.");
        System.out.println();
        System.out.print(help());
        System.out.println();

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            String line = scanner.nextLine();
            System.out.println();

            try {
                result = eval(scanner, line);
                System.out.print(result);
                System.out.println();
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg + "\n\n");
            }
        }
        System.out.println();
    }

    public String eval(Scanner scanner, String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "login" -> login(scanner, params);
                case "register" -> register(scanner, params);
                case "clear" -> clear(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String login(Scanner scanner, String... params) throws ResponseException {
        if (params.length == 2) {
            try {
                LoginRequest request = new LoginRequest(params[0], params[1]);
                server.login(request);
                try {
                    new PostLogin(scanner, url).run();
                    return String.format("Signed in as %s.\n", params[0]);
                } catch (Throwable ex) {
                    System.out.printf("Unable to sign in: %s%n", ex.getMessage());
                }
                
            } catch (Exception e) {
                throw new ResponseException(ResponseException.Code.BadRequest, "Incorrect username or password\n");
            }
        }
        throw new ResponseException(ResponseException.Code.BadRequest, "Expected: <username> <password>\n");
    }

    public String register(Scanner scanner, String... params) throws ResponseException {
        if (params.length == 3) {
            try {
                UserData request = new UserData(params[0], params[1], params[2]);
                server.register(request);
                try {
                    new PostLogin(scanner, url).run();
                    return String.format("Signed in as %s.\n", params[0]);
                } catch (Throwable ex) {
                    System.out.printf("Unable to sign in: %s%n", ex.getMessage());
                }
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
                return "Data Cleared\n";
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
