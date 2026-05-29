package client;

import server.ServerFacade;

import java.util.Arrays;
import java.util.Scanner;

import exception.ResponseException;

public class PreLogin {
    private String visitorName = null;
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
                result = eval(line);
                System.out.print(result);
                System.out.println();
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
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String login(String... params) throws ResponseException {
        if (params.length == 2) {
            visitorName = String.join(" ", params);
            server.login(visitorName);
            return String.format("You signed in as %s.", params[0]);
        }
        throw new ResponseException(ResponseException.Code.BadRequest, "Expected: <username> <password>\n");
    }

    public String register(String... params) throws ResponseException {
        if (params.length == 3) {
            visitorName = String.join(" ", params);
            server.register(visitorName);
            return String.format("You signed in as %s.", params[0]);
        }
        throw new ResponseException(ResponseException.Code.BadRequest, "Expected: <username> <email> <password>\n");
    }

    public String help() {
        return """
                - login <username> <password>
                - register <username> <email> <password>
                - help
                - quit
                """;
    }
}
