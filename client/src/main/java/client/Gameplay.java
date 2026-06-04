package client;

import java.util.Arrays;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.io.FileDescriptor;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import exception.ResponseException;
import server.ServerFacade;

import ui.EscapeSequences;

public class Gameplay {
    private final ServerFacade server;

    public static final String WHITE  = "\u001B[48;5;230m";
    public static final String BLACK  = "\u001B[48;5;235m";

    public static final String PURPLE = "\u001B[35m";

    public static final String WHITE_PIECE = EscapeSequences.SET_TEXT_COLOR_RED;
    public static final String BLACK_PIECE = "\u001B[38;5;233m";

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

        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, StandardCharsets.UTF_8));
        
        draw(reversed);

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("Closing chess")) {
            String line = scanner.nextLine();
            System.out.println();

            try {
                result = eval(line);
                System.out.print(PURPLE + result + RESET);
                System.out.println();
                if (!inGame) {
                    try {
                        new PostLogin(auth, url).run();
                        return;
                    } catch (Throwable ex) {
                        System.out.printf("Unable to leave game: %s%n", ex.getMessage());
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
                case "leave" -> leaveGame();
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
        if (row == 7) {
            return BLACK_PIECE + EscapeSequences.BLACK_PAWN + RESET;
        }
        if (row == 2) {
            return WHITE_PIECE + EscapeSequences.BLACK_PAWN + RESET;
        }
        if (row == 8) {
            if (col == 1 || col == 8) {
                return BLACK_PIECE + EscapeSequences.BLACK_ROOK + RESET;
            }
            if (col == 2 || col == 7) {
                return BLACK_PIECE + EscapeSequences.BLACK_KNIGHT + RESET;
            }
            if (col == 3 || col == 6) {
                return BLACK_PIECE + EscapeSequences.BLACK_BISHOP + RESET;
            }
            if ((col == 4 && !reversed) || (col == 5 && reversed)) {
                return BLACK_PIECE + EscapeSequences.BLACK_KING + RESET;
            }
            if ((col == 5 && !reversed) || (col == 4 && reversed)) {
                return BLACK_PIECE + EscapeSequences.BLACK_QUEEN + RESET;
            }
        }
        if (row == 1) {
            if (col == 1 || col == 8) {
                return WHITE_PIECE + EscapeSequences.BLACK_ROOK + RESET;
            }
            if (col == 2 || col == 7) {
                return WHITE_PIECE + EscapeSequences.BLACK_KNIGHT + RESET;
            }
            if (col == 3 || col == 6) {
                return WHITE_PIECE + EscapeSequences.BLACK_BISHOP + RESET;
            }
            if ((col == 4 && !reversed) || (col == 5 && reversed)) {
                return WHITE_PIECE + EscapeSequences.BLACK_KING + RESET;
            }
            if ((col == 5 && !reversed) || (col == 4 && reversed)) {
                return WHITE_PIECE + EscapeSequences.BLACK_QUEEN + RESET;
            }
        }

        return EscapeSequences.EMPTY;
    }

    public String leaveGame() {
        inGame = false;
        return "Exiting game\n";
    }
}
