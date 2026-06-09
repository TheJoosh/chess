package client;

import java.util.Arrays;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.io.FileDescriptor;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import chess.*;

import exception.ResponseException;
import server.ServerFacade;
import model.GameData;

import ui.EscapeSequences;

public class Gameplay {
    private final ServerFacade server;

    public static final String WHITE  = "\u001B[48;5;230m";
    public static final String BLACK  = "\u001B[48;5;235m";

    public static final String PURPLE = "\u001B[35m";

    public static final String WHITE_PIECE = EscapeSequences.SET_TEXT_COLOR_RED;
    public static final String BLACK_PIECE = "\u001B[38;5;233m";

    public static final String RESET = "\u001B[0m";

    boolean inGame = true;
    boolean reversed;
    boolean observing;
    String url;
    String auth;
    GameData game;

    public Gameplay (String auth, String url, boolean reversed, boolean observing, GameData game) throws ResponseException {
        server = new ServerFacade(url);
        this.url = url;
        this.auth = auth;
        this.reversed = reversed;
        this.observing = observing;
        this.game = game;
    }

    public void run() {

        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, StandardCharsets.UTF_8));
        
        draw(reversed, false);

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

    public String draw(boolean reversed, boolean redraw) {
        drawBoard(reversed);

        if (redraw) {
            return "Board redrawn\n";
        }
        return "";
    }

    public void drawBoard(boolean reversed) {
        ChessBoard board = game.game().getBoard();

        if (!reversed) {
            System.out.print("   a  b  c  d  e  f  g  h\n");
            for (int i = 8; i >= 1; i--) {
                System.out.print(i + " ");
                for (int j = 1; j <= 8; j++) {
                    if ((i + j) % 2 != 0) {
                        System.out.print(WHITE + placePiece(board.getPiece(new ChessPosition(i, j))) + RESET);
                    } else {
                        System.out.print(BLACK + placePiece(board.getPiece(new ChessPosition(i, j))) + RESET);
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
                        System.out.print(WHITE + placePiece(board.getPiece(new ChessPosition(i, 9 - j))) + RESET);
                    } else {
                        System.out.print(BLACK + placePiece(board.getPiece(new ChessPosition(i, 9 - j))) + RESET);
                    }
                }
                System.out.print(" " + i + "\n");
            }
            System.out.print("   h  g  f  e  d  c  b  a\n\n");
        }
    }

    private String placePiece(ChessPiece piece) {
        String icon;

        if (piece == null) {
            return EscapeSequences.EMPTY;
        }

        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            icon = WHITE_PIECE;
        } else if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
            icon = BLACK_PIECE;
        } else {
            return EscapeSequences.EMPTY;
        }

        if (piece.getPieceType() == ChessPiece.PieceType.BISHOP) {
            icon = icon + EscapeSequences.BLACK_BISHOP;
        } else if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            icon = icon + EscapeSequences.BLACK_KING;
        } else if (piece.getPieceType() == ChessPiece.PieceType.QUEEN) {
            icon = icon + EscapeSequences.BLACK_QUEEN;
        } else if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
            icon = icon + EscapeSequences.BLACK_ROOK;
        } else if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
            icon = icon + EscapeSequences.BLACK_KNIGHT;
        } else if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            icon = icon + EscapeSequences.BLACK_PAWN;
        } else {
            return EscapeSequences.EMPTY;
        }

        return icon + RESET;
    }

    public String leaveGame() {
        inGame = false;
        return "Exiting game\n";
    }
    
    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "leave" -> leaveGame();
                case "redraw" -> draw(reversed, true);
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public String help() {
        if (observing) {
            return """
                    - showMoves <start square>
                    - redraw
                    - help
                    - leave
                    """;
        }
        return """
                - makeMove <start square> <end square>
                - showMoves <start square>
                - redraw
                - resign
                - help
                - leave
                """;
    }
}