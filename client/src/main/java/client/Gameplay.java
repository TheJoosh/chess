package client;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Scanner;

import exception.ResponseException;
import server.ServerFacade;

import ui.EscapeSequences;

public class Gameplay {
    private final ServerFacade server;

    public static final String WHITE = "\u001B[47m";
    public static final String BLACK = "\u001B[100m";
    public static final String BLACK_PIECE = "\u001B[30m";
    public static final String WHITE_PIECE = "\u001B[37m";
    public static final String RESET = "\u001B[0m";

    boolean inGame = false;
    boolean reversed;
    String url;
    String auth;

    public Gameplay (String auth, String url, boolean reversed) throws ResponseException {
        server = new ServerFacade(url);
        this.url = url;
        this.auth = auth;
        this.reversed = reversed;
    }

    public void run() {
        
        draw(reversed);

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("Closing chess")) {
            String line = scanner.nextLine();
            System.out.println();

            try {
                result = eval(line);
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
                default -> "Closing chess";
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public void draw(boolean reversed) {
        drawBoard(reversed);
    }

    public void drawBoard(boolean reversed) {
        if (!reversed) {
            System.out.print("   a  b  c  d  e  f  g  h\n");
            for (int i = 8; i >= 1; i--) {
                System.out.print(i + " ");
                for (int j = 1; j <= 8; j++) {
                    if ((i + j) % 2 != 0) {
                        System.out.print(WHITE + placePiece(i, j) + RESET);
                    } else {
                        System.out.print(BLACK + placePiece(i, j) + RESET);
                    }
                }
                System.out.print(" " + i + "\n");
            }
            System.out.print("   a  b  c  d  e  f  g  h\n\n");
        } else {
            System.out.print("   h  g  f  e  d  c  b  a\n");
            for (int i = 1; i <= 8; i++) {
                System.out.print(i + " ");
                for (int j = 1; j <= 8; j++) {
                    if ((i + j) % 2 == 0) {
                        System.out.print(WHITE + placePiece(i, j) + RESET);
                    } else {
                        System.out.print(BLACK + placePiece(i, j) + RESET);
                    }
                }
                System.out.print(" " + i + "\n");
            }
            System.out.print("   h  g  f  e  d  c  b  a\n\n");
        }
    }

    private String placePiece(int row, int col) {
        if (row == 2) {
            return EscapeSequences.BLACK_PAWN;
        }
        if (row == 7) {
            return EscapeSequences.WHITE_PAWN;
        }
        if (row == 1) {
            if (col == 1 || col == 8) {
                return EscapeSequences.BLACK_ROOK;
            }
            if (col == 2 || col == 7) {
                return EscapeSequences.BLACK_KNIGHT;
            }
            if (col == 3 || col == 6) {
                return EscapeSequences.BLACK_BISHOP;
            }
            if ((col == 4 && !reversed) || (col == 5 && reversed)) {
                return EscapeSequences.BLACK_KING;
            }
            if ((col == 5 && !reversed) || (col == 4 && reversed)) {
                return EscapeSequences.BLACK_QUEEN;
            }
        }
        if (row == 8) {
            if (col == 1 || col == 8) {
                return EscapeSequences.WHITE_ROOK;
            }
            if (col == 2 || col == 7) {
                return EscapeSequences.WHITE_KNIGHT;
            }
            if (col == 3 || col == 6) {
                return EscapeSequences.WHITE_BISHOP;
            }
            if ((col == 4 && reversed) || (col == 5 && !reversed)) {
                return EscapeSequences.WHITE_KING;
            }
            if ((col == 5 && reversed) || (col == 4 && !reversed)) {
                return EscapeSequences.WHITE_QUEEN;
            }
        }

        return EscapeSequences.EMPTY;
    }
}
