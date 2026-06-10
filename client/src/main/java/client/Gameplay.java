package client;

import java.util.Arrays;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.io.FileDescriptor;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.Collection;

import chess.*;

import exception.ResponseException;
import server.ServerFacade;
import model.GameData;

import ui.EscapeSequences;

public class Gameplay {
    private final ServerFacade server;

    public static final String WHITE  = "\u001B[48;5;230m";
    public static final String BLACK  = "\u001B[48;5;235m";
    public static final String GREENW = EscapeSequences.SET_BG_COLOR_GREEN;
    public static final String GREENB = EscapeSequences.SET_BG_COLOR_DARK_GREEN;

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
    ChessGame chessGame;

    public Gameplay (String auth, String url, boolean reversed, boolean observing, GameData game) throws ResponseException {
        server = new ServerFacade(url);
        this.url = url;
        this.auth = auth;
        this.reversed = reversed;
        this.observing = observing;
        this.game = game;
        this.chessGame = game.game();
    }

    public void run() {

        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, StandardCharsets.UTF_8));
        
        draw(reversed, false, null);

        @SuppressWarnings("resource")
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

    public String draw(boolean reversed, boolean redraw, Collection<ChessMove> moves) {
        drawBoard(reversed, moves);

        if (redraw) {
            return "Board redrawn\n";
        }
        return "";
    }

    public void drawBoard(boolean reversed, Collection<ChessMove> moves) {
        ChessBoard board = game.game().getBoard();

        int iReversed;
        int jReversed;

        printCoords(reversed);

        for (int i = 8; i >= 1; i--) {
            if (reversed) {
                iReversed = 9 - i;
            } else {
                iReversed = i;
            }
            System.out.print(iReversed + " ");

            for (int j = 1; j <= 8; j++) {
                if (reversed) {
                    jReversed = 9 - j;
                } else {
                    jReversed = j;
                }
                drawSquare(reversed, iReversed, jReversed, board, moves);
            }
            System.out.print(" " + iReversed + "\n");
        }
        
        printCoords(reversed);
        System.out.println();
    }

    private void printCoords(boolean reversed) {
        if (!reversed) {
            System.out.print("   a  b  c  d  e  f  g  h\n");
        } else {
            System.out.print("   h  g  f  e  d  c  b  a\n");
        }
    }

    private void drawSquare (boolean reversed, int i, int j, ChessBoard board, Collection<ChessMove> moves) {

        String white = WHITE;
        String black = BLACK;

        if (moves != null) {
            for (ChessMove move : moves) {
                if (move.getEndPosition().getRow() == i && move.getEndPosition().getColumn() == j) {
                    white = GREENW;
                    black = GREENB;
                }
            }
        }

        if ((i + j) % 2 != 0) {
            System.out.print(white + placePiece(board.getPiece(new ChessPosition(i, j))) + RESET);
        } else {
            System.out.print(black + placePiece(board.getPiece(new ChessPosition(i, j))) + RESET);
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
                case "redraw" -> draw(reversed, true, null);
                case "moves" -> showMoves(params);
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public String showMoves(String... params) throws ResponseException {
        if (params.length == 1) {
            String square = params[0];
            if (square.length() == 2) {
                int row;

                try {
                    row = Integer.parseInt(square.substring(1, 2));
                } catch (Exception e) {
                    throw new ResponseException(ResponseException.Code.BadRequest, "Square must have format <letter><number>\n");
                }

                String colLetter = square.substring(0, 1);
                int col = switch (colLetter) {
                    case "a" -> 1;
                    case "b" -> 2;
                    case "c" -> 3;
                    case "d" -> 4;
                    case "e" -> 5;
                    case "f" -> 6;
                    case "g" -> 7;
                    case "h" -> 8;
                    default -> 0;
                };

                if (col != 0 && row <= 8 && row >= 1) {
                    Collection<ChessMove> moves;
                    try {
                        moves = chessGame.validMoves(new ChessPosition(row, col));
                    } catch (Exception e) {
                        throw new ResponseException(ResponseException.Code.ServerError, "Unable to access moves");
                    }
                    draw(reversed, false, moves);
                    return "Calculated possible moves\n";
                } else {
                    throw new ResponseException(ResponseException.Code.BadRequest, "Square must be within range a1-h8\n");
                }
            }

            throw new ResponseException(ResponseException.Code.BadRequest, "Square must have format <letter><number>\n");

        } 
        throw new ResponseException(ResponseException.Code.BadRequest, "Expected: <start square>\n");
    }

    public String help() {
        if (observing) {
            return """
                    - moves <start square>
                    - redraw
                    - help
                    - leave
                    """;
        }
        return """
                - makeMove <start square> <end square>
                - moves <start square>
                - redraw
                - resign
                - help
                - leave
                """;
    }
}