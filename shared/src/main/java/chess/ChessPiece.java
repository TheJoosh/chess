package chess;

import java.util.Collection;

import chess.ChessGame.TeamColor;

import java.util.ArrayList;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates if a piece can move to a given position
     * If the move is possible, adds it to the list
     * 
     * @param board the chess board
     * @param myPosition the starting position of the piece
     * @param newPosition the position to which the piece is trying to move
     * @param piece the piece in question
     * @param moves a list of possible moves
     * @return whether the position is occupied by another piece
     */
    public boolean calculatePosition(
                    ChessBoard board, 
                    ChessPosition myPosition, 
                    ChessPosition newPosition, 
                    ChessPiece piece, 
                    ArrayList<ChessMove> moves
                ) {
        if (board.getPiece(newPosition) != null) {
            if (board.getPiece(newPosition).getTeamColor() != piece.getTeamColor()) {
                moves.add(new ChessMove(myPosition, newPosition, null));
            }
            return true;
        } else {
            moves.add(new ChessMove(myPosition, newPosition, null));
        }
        return false;
    }

    /**
     * Calculates the bounds of a piece's movement based on whether or not it is the king
     * 
     * @param axis the position of the piece along the given axis
     * @param direction the direction of motion being measured
     * @param king whether or not the piece is a king
     * @return the upper and lower bounds
     */
    public int[] bounds(int axis, int direction, boolean king) {
        int[] bounds = {7,0};

        if (king) {
            if (direction == 1 && axis + 1 <= 7) {
                bounds[0] = axis + 1;
            } else if (direction == -1 && axis - 1 >= 0) {
                bounds[1] = axis - 1;
            }
        }

        return bounds;
    }

    /**
     * Calculates all diagonal positions until it reaches an occupied position
     * 
     * @param board the chess board
     * @param myPosition the starting position of the piece
     * @param piece the piece in question
     * @param moves a list of possible moves
     * @param row the row of the current position
     * @param column the column of the current position
     * @param direction the vertical direction of motion being measured
     * @param king whether or not the piece is a king
     */
    public void calculateDiagonal(
                    ChessBoard board, 
                    ChessPosition myPosition, 
                    ChessPiece piece, 
                    ArrayList<ChessMove> moves, 
                    int row, 
                    int column, 
                    int direction, 
                    boolean king
                ) {

        //set bounds for the king
        int[] bounds = bounds(row, direction, king);

        int offset = 1;
        boolean rblocked = false;
        boolean lblocked = false;

        for (int i = row + direction; i >= bounds[1] && i <= bounds[0]; i += direction) {

                //calculate left
                if (!lblocked){
                    if (column - offset < 0) {
                        lblocked = true;
                    } else {
                        lblocked = calculatePosition(board, myPosition, new ChessPosition(i + 1, column - offset + 1), piece, moves);
                    }
                }

                //calculate right
                if (!rblocked) {
                    if (column + offset > 7) {
                        rblocked = true;
                    } else {
                        rblocked = calculatePosition(board, myPosition, new ChessPosition(i + 1, column + offset + 1), piece, moves);
                    }
                }

                offset++;
            }
    }

    /**
     * Calculates in all diagonal directions
     * 
     * @param board the chess board
     * @param myPosition the starting position of the piece
     * @param piece the piece in question
     * @param moves a list of possible moves
     * @param row the row of the current position
     * @param column the column of the current position
     * @param king whether or not the piece is a king
     */
    public void calculateBishop(
                    ChessBoard board, 
                    ChessPosition myPosition, 
                    ChessPiece piece, 
                    ArrayList<ChessMove> moves, 
                    int row, 
                    int column, 
                    boolean king
                ) {

        //calculate backward
            calculateDiagonal(board, myPosition, piece, moves, row, column, -1, king);

            //calculate forward
            calculateDiagonal(board, myPosition, piece, moves, row, column, 1, king);
    }

    /**
     * Calculates all vertical and horizontal positions until it reaches an occupied position
     * 
     * @param board the chess board
     * @param myPosition the starting position of the piece
     * @param piece the piece in question
     * @param moves a list of possible moves
     * @param row the row of the current position
     * @param column the column of the current position
     * @param direction the vertical direction of motion being measured
     * @param king whether or not the piece is a king
     */
    public void calculateStraight(
                    ChessBoard board, 
                    ChessPosition myPosition, 
                    ChessPiece piece, 
                    ArrayList<ChessMove> moves, 
                    int row, 
                    int column, 
                    int direction, 
                    boolean king
                ) {
        
        //set bounds for the king
        int[] rBounds = bounds(row, direction, king);
        int[] cBounds = bounds(column, direction, king);
        
        //calculate vertically
        for (int i = row + direction; i <= rBounds[0] && i >= rBounds[1]; i += direction) {
            if (calculatePosition(board, myPosition, new ChessPosition(i + 1, column + 1), piece, moves)) {
                break;
            }
        }

        //calculate horizontally
        for (int i = column + direction; i <= cBounds[0] && i >= cBounds[1]; i += direction) {
            if (calculatePosition(board, myPosition, new ChessPosition(row + 1, i + 1), piece, moves)) {
                break;
            }
        }
    }

    /**
     * Calculates horizontal L movements
     * 
     * @param board the chess board
     * @param myPosition the starting position of the piece
     * @param piece the piece in question
     * @param moves a list of possible moves
     * @param row the row of the current position
     * @param column the column of the current position
     * @param direction the vertical direction of motion being measured
     */
    public void calculateKnightHorizontal(
                    ChessBoard board, 
                    ChessPosition myPosition, 
                    ChessPiece piece, 
                    ArrayList<ChessMove> moves, 
                    int row, 
                    int column, 
                    int direction
                ) {
        
        //calculate forward
        if (row + 1 <= 7) {
            calculatePosition(board, myPosition, new ChessPosition(row + 2, column + 1 + direction * 2), piece, moves);
        }

        //calculate backward
        if (row - 1 >= 0) {
            calculatePosition(board, myPosition, new ChessPosition(row, column + 1 + direction * 2), piece, moves);
        }
    }

    /**
     * Calculates vertical L movements
     * 
     * @param board the chess board
     * @param myPosition the starting position of the piece
     * @param piece the piece in question
     * @param moves a list of possible moves
     * @param row the row of the current position
     * @param column the column of the current position
     * @param direction the vertical direction of motion being measured
     */
    public void calculateKnightVertical(
                    ChessBoard board, 
                    ChessPosition myPosition, 
                    ChessPiece piece, 
                    ArrayList<ChessMove> moves, 
                    int row, 
                    int column, 
                    int direction
                ) {
        
        //calculate right
        if (column + 1 <= 7) {
            calculatePosition(board, myPosition, new ChessPosition(row + 1 + (direction * 2), column + 2), piece, moves);
        }

        //calculate left
        if (column - 1 >= 0) {
            calculatePosition(board, myPosition, new ChessPosition(row + 1 + (direction * 2), column), piece, moves);
        }
    }

    /**
     * Calculates in all four cardinal directions
     * 
     * @param board the chess board
     * @param myPosition the starting position of the piece
     * @param piece the piece in question
     * @param moves a list of possible moves
     * @param row the row of the current position
     * @param column the column of the current position
     * @param king whether or not the piece is a king
     */
    public void calculateRook(
                    ChessBoard board, 
                    ChessPosition myPosition, 
                    ChessPiece piece, 
                    ArrayList<ChessMove> moves, 
                    int row, 
                    int column, 
                    boolean king
                ) {

        //calculate in the positive directions
        calculateStraight(board, myPosition, piece, moves, row, column, 1, king);
        
        //calculate in the negative directions
        calculateStraight(board, myPosition, piece, moves, row, column, -1, king);
    }

    /**
     * Calculates whether a pawn is promoted
     * 
     * @param myPosition the starting position of the piece
     * @param newPosition the position to which the piece is trying to move
     * @param moves a list of possible moves
     * @param row the row of the current position
     * @param direction the vertical direction of motion being measured
     * @param end the end row of a pawn
     */
    public void calculatePawnPromotion(
                    ChessPosition myPosition, 
                    ChessPosition newPosition, 
                    ArrayList<ChessMove> moves, 
                    int row, 
                    int direction, 
                    int end
                ) {
        if (row + direction == end) {
            moves.add(new ChessMove(myPosition, newPosition, PieceType.BISHOP));
            moves.add(new ChessMove(myPosition, newPosition, PieceType.QUEEN));
            moves.add(new ChessMove(myPosition, newPosition, PieceType.ROOK));
            moves.add(new ChessMove(myPosition, newPosition, PieceType.KNIGHT));
        } else {
            moves.add(new ChessMove(myPosition, newPosition, null));
        }
    }

    /**
     * Calculates a pawn capturing an enemy piece
     * 
     * @param board the chess board
     * @param myPosition the starting position of the piece
     * @param piece the piece in question
     * @param moves a list of possible moves
     * @param row the row of the current position
     * @param column the column of the current position
     * @param xDirection the horizontal direction of a capturing pawn
     * @param yDirection the vertical direction of a capturing pawn
     * @param end the end row of a pawn
     */
    public void calculatePawnCapture(
                    ChessBoard board, 
                    ChessPosition myPosition, 
                    ChessPiece piece, 
                    ArrayList<ChessMove> moves, 
                    int row, 
                    int column, 
                    int xDirection, 
                    int yDirection, 
                    int end
                ) {

        if (column + xDirection <= 7 && column + xDirection >= 0) {

                ChessPosition newPosition = new ChessPosition(row + 1 + yDirection, column + 1 + xDirection);
                if (board.getPiece(newPosition) != null) {
                    if (board.getPiece(newPosition).getTeamColor() != piece.getTeamColor()) {

                        //calculate capture promotion
                        calculatePawnPromotion(myPosition, newPosition, moves, row, yDirection, end);
                    }
                }
            }
    }

    /**
     * Calculates the movement of a pawn
     * 
     * @param board the chess board
     * @param myPosition the starting position of the piece
     * @param piece the piece in question
     * @param moves a list of possible moves
     * @param row the row of the current position
     * @param column the column of the current position
     * @param direction the vertical direction of motion being measured
     * @param start the starting row of a pawn
     * @param end the end row of a pawn
     */
    public void calculatePawn(ChessBoard board, 
                    ChessPosition myPosition, 
                    ChessPiece piece, 
                    ArrayList<ChessMove> moves, 
                    int row, 
                    int column, 
                    int direction, 
                    int start, 
                    int end
                ) {

        ChessPosition newPosition;

        if (row + direction <= 7 && row + direction >= 0) {

            //calculate moving forward
            if (board.getPiece(new ChessPosition(row + 1 + direction, column + 1)) == null) {

                //calculate move from start
                newPosition = new ChessPosition(row + 1 + (direction * 2), column + 1);
                if (row == start && board.getPiece(newPosition) == null) {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                }

                //calculate promotion
                newPosition = new ChessPosition(row + 1 + direction, column + 1);
                calculatePawnPromotion(myPosition, newPosition, moves, row, direction, end);
            }
            
            //calculate right capture
            calculatePawnCapture(board, myPosition, piece, moves, row, column, 1, direction, end);

            //calculate left capture
            calculatePawnCapture(board, myPosition, piece, moves, row, column, -1, direction, end);
        }
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @param board the chess board
     * @param myPosition the starting position of the piece
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {

        ChessPiece piece = board.getPiece(myPosition);
        ArrayList<ChessMove> moves = new ArrayList<>();
        
        int column = myPosition.getColumn() - 1;
        int row = myPosition.getRow() - 1;

        if (piece.getPieceType() == PieceType.KING) {

            //calculate diagonals
            calculateBishop(board, myPosition, piece, moves, row, column, true);

            //calculate in all four cardinal directions
            calculateRook(board, myPosition, piece, moves, row, column, true);
        } else if (piece.getPieceType() == PieceType.QUEEN) {

            //calculate diagonals
            calculateBishop(board, myPosition, piece, moves, row, column, false);

            //calculate in all four cardinal directions
            calculateRook(board, myPosition, piece, moves, row, column, false);
        } else if (piece.getPieceType() == PieceType.ROOK) {

            //calculate in all four cardinal directions
            calculateRook(board, myPosition, piece, moves, row, column, false);
        } else if (piece.getPieceType() == PieceType.BISHOP) {

            //calculate diagonals
            calculateBishop(board, myPosition, piece, moves, row, column, false);
        } else if (piece.getPieceType() == PieceType.KNIGHT) {

            //calculate forward
            if (row + 2 <= 7) {
                calculateKnightVertical(board, myPosition, piece, moves, row, column, 1);
            }

            //calculate backward
            if (row - 2 >= 0) {
                calculateKnightVertical(board, myPosition, piece, moves, row, column, -1);
            }

            //calculate right
            if (column + 2 <= 7) {
                calculateKnightHorizontal(board, myPosition, piece, moves, row, column, 1);
            }

            //calculate left
            if (column - 2 >= 0) {
                calculateKnightHorizontal(board, myPosition, piece, moves, row, column, -1);
            }
        } else if (piece.getPieceType() == PieceType.PAWN) {

            int direction;
            int start;
            int end;
            
            //set parameters based on color
            if (piece.getTeamColor() == TeamColor.WHITE) {
                direction = 1;
                start = 1;
                end = 7;
            } else {
                direction = -1;
                start = 6;
                end = 0;
            }

            //calculate moves
            calculatePawn(board, myPosition, piece, moves, row, column, direction, start, end);
        }

        return moves;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(pieceColor, type);
    }

    @Override
    public String toString() {
        char letter = switch (type) {
            case KING -> 'k';
            case QUEEN -> 'q';
            case ROOK -> 'r';
            case BISHOP -> 'b';
            case KNIGHT -> 'n';
            case PAWN -> 'p';
        };
        return pieceColor == ChessGame.TeamColor.WHITE
                ? Character.toString(Character.toUpperCase(letter))
                : Character.toString(letter);
    }
}
