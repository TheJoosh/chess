package client;

import java.util.Arrays;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.io.FileDescriptor;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.Collection;

import chess.*;
import client.websocket.*;
import websocket.messages.ServerMessage;
import exception.ResponseException;
import model.GameData;

import ui.EscapeSequences;

public class Gameplay implements NotificationHandler {
    private final WebSocketFacade ws;

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
    String username;
    GameData game;
    ChessGame chessGame;
    ChessGame.TeamColor team;

    public Gameplay (String auth, String url, boolean reversed, boolean observing, GameData game, String username) throws ResponseException {
        ws = new WebSocketFacade(url, this);
        this.url = url;
        this.auth = auth;
        this.reversed = reversed;
        this.observing = observing;
        this.game = game;
        this.chessGame = game.game();
        this.username = username;
        if (reversed) {
            team = ChessGame.TeamColor.BLACK;
        } else {
            team = ChessGame.TeamColor.WHITE;
        }
    }

    public void run() throws ResponseException {

        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, StandardCharsets.UTF_8));

        ChessGame.TeamColor team = null;

        if (!observing) {
            if (!reversed) {
                team = ChessGame.TeamColor.WHITE;
            } else {
                team = ChessGame.TeamColor.BLACK;
            }
        }

        ws.joinGame(username, auth, game.gameID(), chessGame, team);

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
                        new PostLogin(auth, url, username).run();
                        return;
                    } catch (Throwable ex) {
                        System.out.printf("Unable to leave game: %s%n\n", ex.getMessage());
                    }
                }
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg + "\n\n");
            }
        }
        System.out.println();
    }

    public String resign() throws ResponseException {

        @SuppressWarnings("resource")
        Scanner scanner = new Scanner(System.in);

        var result = "";
        while (!result.equals("y") && !result.equals("Y") && !result.equals("n") && !result.equals("N")) {
            System.out.printf(PURPLE +"Confirm game resignation [Y/N]\n\n" + RESET);

            result = scanner.nextLine();
            System.out.println();

            if (result.equals("Y") || result.equals("y")) {
                inGame = false;
                ws.resign(username, auth, game.gameID());
                return "Resigned game\n";
            }
       }

        return "Resignation canceled\n";
    }

    public String draw(boolean reversed, boolean redraw, Collection<ChessMove> moves, ChessGame gameState) {
        drawBoard(reversed, moves, gameState);

        if (redraw) {
            return "Board redrawn\n";
        }
        return "";
    }

    public void drawBoard(boolean reversed, Collection<ChessMove> moves, ChessGame gameState) {
        ChessBoard board = gameState.getBoard();

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

    public String leaveGame() throws ResponseException {
        inGame = false;

        ChessGame.TeamColor team = null;
        if (!observing) {
            if (!reversed) {
                team = ChessGame.TeamColor.WHITE;
            } else {
                team = ChessGame.TeamColor.BLACK;
            }
        }

        ws.leaveGame(username, auth, game.gameID(), team);

        return "Exiting game\n";
    }
    
    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "leave" -> leaveGame();
                case "quit" -> leaveGame();
                case "redraw" -> draw(reversed, true, null, game.game());
                case "draw" -> draw(reversed, true, null, game.game());
                case "seemoves" -> showMoves(params);
                case "showmoves" -> showMoves(params);
                case "resign" -> resign();
                case "move" -> makeMove(params);
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public int[] coordify(String... params) throws ResponseException {
        int[] coords = new int[4];
        int i = 0;

        for (String item : params) {
            if (item.length() == 2) {
                int row;

                try {
                    row = Integer.parseInt(item.substring(1, 2));
                } catch (Exception e) {
                    throw new ResponseException(ResponseException.Code.BadRequest, "Position must have format <letter><number>\n");
                }

                String colLetter = item.substring(0, 1);
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

                coords[i] = row;
                coords[i + 1] = col;
                i += 2;
            } else {
                throw new ResponseException(ResponseException.Code.BadRequest, "Position must have format <letter><number>\n");
            }
        }

        return coords;
    }

    public String makeMove(String... params) throws ResponseException {
        if (observing) {
            throw new ResponseException(ResponseException.Code.BadRequest, "Observers cannot make moves\n");
        }

        if (
            (chessGame.getTeamTurn() == ChessGame.TeamColor.BLACK && !reversed) || 
            (chessGame.getTeamTurn() == ChessGame.TeamColor.WHITE && reversed)
        ) {
            throw new ResponseException(ResponseException.Code.BadRequest, "Cannot move out of turn\n");
        }

        if (params.length != 2 && params.length != 3) {
            throw new ResponseException(ResponseException.Code.BadRequest, "A Expected: <start position> <end position>\n");
        }

        ChessPiece.PieceType promotion = null;
        int[] coords = coordify(params[0], params[1]);
        ChessPiece piece = chessGame.getBoard().getPiece(new ChessPosition(coords[0], coords[1]));

        if (params.length == 3) {
            if (piece.getPieceType() != ChessPiece.PieceType.PAWN) {
                throw new ResponseException(ResponseException.Code.BadRequest, "B Expected: <start position> <end position>\n");
            }

            if (
                (piece.getTeamColor() == ChessGame.TeamColor.BLACK && coords[0] != 2) || 
                (piece.getTeamColor() == ChessGame.TeamColor.WHITE && coords[0] != 7)
            ) {
                throw new ResponseException(ResponseException.Code.BadRequest, "C Expected: <start position> <end position>\n");
            }

            promotion = parsePromotion(params[2]);

            if (promotion == null) {
                throw new ResponseException(ResponseException.Code.BadRequest, "Invalid promotion piece");
            }
        } else if (piece.getPieceType() != ChessPiece.PieceType.PAWN) {
            if (
                (piece.getTeamColor() == ChessGame.TeamColor.BLACK && coords[0] == 2) || 
                (piece.getTeamColor() == ChessGame.TeamColor.WHITE && coords[0] == 7)
            ) {
                throw new ResponseException(ResponseException.Code.BadRequest, 
                                            "Pawn must promote: <start position> <end position> <promotion piece>\n"
                                            );
            }
        }

        if(coords[1] != 0 && coords[3] != 0 && coords[0] <= 8 && coords[0] >= 1 && coords[2] <= 8 && coords[2] >= 1) {
            ChessMove move = new ChessMove(new ChessPosition(coords[0], coords[1]), new ChessPosition(coords[2], coords[3]), promotion);

            ws.makeMove(username, auth, game.gameID(), team, move);

            return "Moved " + piece.getPieceType().toString() + " from " + params[0] + " to " + params[1] + "\n";
        } else {
            throw new ResponseException(ResponseException.Code.BadRequest, "Position must be within range a1-h8\n");
        }
    }

    private ChessPiece.PieceType parsePromotion (String input) {
        return switch (input) {
                case "queen" -> ChessPiece.PieceType.QUEEN;
                case "Queen" -> ChessPiece.PieceType.QUEEN;
                case "q" -> ChessPiece.PieceType.QUEEN;
                case "Q" -> ChessPiece.PieceType.QUEEN;
                case "QUEEN" -> ChessPiece.PieceType.QUEEN;
                case "bishop" -> ChessPiece.PieceType.BISHOP;
                case "Bishop" -> ChessPiece.PieceType.BISHOP;
                case "b" -> ChessPiece.PieceType.BISHOP;
                case "B" -> ChessPiece.PieceType.BISHOP;
                case "BISHOP" -> ChessPiece.PieceType.BISHOP;
                case "rook" -> ChessPiece.PieceType.ROOK;
                case "Rook" -> ChessPiece.PieceType.ROOK;
                case "r" -> ChessPiece.PieceType.ROOK;
                case "R" -> ChessPiece.PieceType.ROOK;
                case "ROOK" -> ChessPiece.PieceType.ROOK;
                case "knight" -> ChessPiece.PieceType.KNIGHT;
                case "KNIGHT" -> ChessPiece.PieceType.KNIGHT;
                case "k" -> ChessPiece.PieceType.KNIGHT;
                case "K" -> ChessPiece.PieceType.KNIGHT;
                case "n" -> ChessPiece.PieceType.KNIGHT;
                case "N" -> ChessPiece.PieceType.KNIGHT;
                case "Knight" -> ChessPiece.PieceType.KNIGHT;
                default -> null;
            };
    }

    public String showMoves(String... params) throws ResponseException {
        if (params.length == 1) {

            int[] coords = coordify(params);

            if (coords[1] != 0 && coords[0] <= 8 && coords[0] >= 1) {
                Collection<ChessMove> moves;
                try {
                    moves = chessGame.validMoves(new ChessPosition(coords[0], coords[1]));
                } catch (Exception e) {
                    throw new ResponseException(ResponseException.Code.BadRequest, "No piece at that position\n");
                }

                if (moves.isEmpty()) {
                    throw new ResponseException(ResponseException.Code.BadRequest, "Piece cannot move\n");
                }

                draw(reversed, false, moves, game.game());
            } else {
                throw new ResponseException(ResponseException.Code.BadRequest, "Position must be within range a1-h8\n");
            }

            return "Calculated possible moves\n";
        }

        throw new ResponseException(ResponseException.Code.BadRequest, "Expected: <start position>\n");
    }

    public String help() {
        if (observing) {
            return """
                    - seemoves <start position>
                    - redraw
                    - help
                    - leave
                    """;
        }
        return """
                - move <start position> <end position>
                - seemoves <start position>
                - redraw
                - resign
                - help
                - leave
                """;
    }

    public void notify(ServerMessage message) {

        if (message.getGame() != null) {
            draw(reversed, false, null, message.getGame());
            game = new GameData(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), message.getGame());
            chessGame = game.game();
        }

        System.out.println(PURPLE + message.getMessage() + "\n" + RESET);
    }
}