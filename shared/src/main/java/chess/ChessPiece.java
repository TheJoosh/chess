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
     */
    public boolean checkPosition(ChessBoard board, ChessPosition myPosition, ChessPosition newPosition, ChessPiece piece, ArrayList<ChessMove> moves) {
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
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {

        ChessPiece piece = board.getPiece(myPosition);
        ArrayList<ChessMove> moves = new ArrayList<>();
        
        int column = myPosition.getColumn() - 1;
        int row = myPosition.getRow() - 1;
        ChessPosition newPosition;

        if (piece.getPieceType() == PieceType.KING) {

            if (row + 1 <= 7) {

                //check forward
                newPosition = new ChessPosition(row + 2, column + 1);
                checkPosition(board, myPosition, newPosition, piece, moves);

                //check forward right
                newPosition = new ChessPosition(row + 2, column + 2);
                if (column + 1 <= 7) {
                    checkPosition(board, myPosition, newPosition, piece, moves);
                }

                //check forward left
                newPosition = new ChessPosition(row + 2, column);
                if (column - 1 >= 0) {
                    checkPosition(board, myPosition, newPosition, piece, moves);
                }
            }
            if (row - 1 >= 0) {

                //check backward 
                newPosition = new ChessPosition(row, column + 1);
                checkPosition(board, myPosition, newPosition, piece, moves);

                //check backward right
                newPosition = new ChessPosition(row, column + 2);
                if (column + 1 <= 7) {
                    checkPosition(board, myPosition, newPosition, piece, moves);
                }

                //check backward left
                newPosition = new ChessPosition(row, column);
                if (column - 1 >= 0) {
                    checkPosition(board, myPosition, newPosition, piece, moves);
                }
            }

            //check right
            newPosition = new ChessPosition(row + 1, column + 2);
            if (column + 1 <= 7) {
                checkPosition(board, myPosition, newPosition, piece, moves);
            }

            //check left
            newPosition = new ChessPosition(row + 1, column);
            if (column - 1 >= 0) {
                checkPosition(board, myPosition, newPosition, piece, moves);
            }
        }

        if (piece.getPieceType() == PieceType.QUEEN) {
            
            int offset = 1;
            boolean rblocked = false;
            boolean lblocked = false;

            //Check backward diagonals
            for (int i = row - 1; i >= 0; i--) {

                //check backward left
                if (!lblocked){
                    if (column - offset < 0) {
                        lblocked = true;
                    } else {
                        newPosition = new ChessPosition(i + 1, column - offset + 1);
                        lblocked = checkPosition(board, myPosition, newPosition, piece, moves);
                    }
                }

                //check backward right
                if (!rblocked) {
                    if (column + offset > 7) {
                        rblocked = true;
                    } else {
                        newPosition = new ChessPosition(i + 1, column + offset + 1);
                        rblocked = checkPosition(board, myPosition, newPosition, piece, moves);
                    }
                }

                offset++;
            }

            //reset variables
            offset = 1;
            rblocked = false;
            lblocked = false;

            //check forward diagonals
            for (int i = row + 1; i <= 7; i++) {

                //check forward left
                if (!lblocked) {
                    if (column - offset < 0) {
                        lblocked = true;
                    } else {
                        newPosition = new ChessPosition(i + 1, column - offset + 1);
                        lblocked = checkPosition(board, myPosition, newPosition, piece, moves);
                    }
                }

                //check forward right
                if (!rblocked) {
                    if (column + offset > 7) {
                        rblocked = true;
                    } else {
                        newPosition = new ChessPosition(i + 1, column + offset + 1);
                        rblocked = checkPosition(board, myPosition, newPosition, piece, moves);
                    }
                }

                offset++;
            }

            //check forward
            for (int i = row + 1; i <= 7; i++) {

                newPosition = new ChessPosition(i + 1, column + 1);
                if (checkPosition(board, myPosition, newPosition, piece, moves)) {
                    break;
                }
            }
            
            //check backward
            for (int i = row - 1; i >= 0; i--) {

                newPosition = new ChessPosition(i + 1, column + 1);
                if (checkPosition(board, myPosition, newPosition, piece, moves)) {
                    break;
                }
            }

            //check left
            for (int i = column - 1; i >= 0; i--) {

                newPosition = new ChessPosition(row + 1, i + 1);
                if (checkPosition(board, myPosition, newPosition, piece, moves)) {
                    break;
                }
            }

            //check right
            for (int i = column + 1; i <= 7; i++) {

                newPosition = new ChessPosition(row + 1, i + 1);
                if (checkPosition(board, myPosition, newPosition, piece, moves)) {
                    break;
                }
            }
        }

        if (piece.getPieceType() == PieceType.ROOK) {

            //check forward
            for (int i = row + 1; i <= 7; i++) {

                newPosition = new ChessPosition(i + 1, column + 1);
                if (checkPosition(board, myPosition, newPosition, piece, moves)) {
                    break;
                }
            }
            
            //check backward
            for (int i = row - 1; i >= 0; i--) {
                newPosition = new ChessPosition(i + 1, column + 1);
                if (checkPosition(board, myPosition, newPosition, piece, moves)) {
                    break;
                }
            }

            //check left
            for (int i = column - 1; i >= 0; i--) {

                newPosition = new ChessPosition(row + 1, i + 1);
                if (checkPosition(board, myPosition, newPosition, piece, moves)) {
                    break;
                }
            }

            //check right
            for (int i = column + 1; i <= 7; i++) {

                newPosition = new ChessPosition(row + 1, i + 1);
                if (checkPosition(board, myPosition, newPosition, piece, moves)) {
                    break;
                }
            }
        }

        if (piece.getPieceType() == PieceType.BISHOP) {

            int offset = 1;
            boolean rblocked = false;
            boolean lblocked = false;

            //check backward diagonals
            for (int i = row - 1; i >= 0; i--) {

                //check backward left
                if (!lblocked){
                    if (column - offset < 0) {
                        lblocked = true;
                    } else {

                        newPosition = new ChessPosition(i + 1, column - offset + 1);
                        if (board.getPiece(newPosition) != null) {
                            lblocked = true;
                            if (board.getPiece(newPosition).getTeamColor() != piece.getTeamColor()) {
                                moves.add(new ChessMove(myPosition, newPosition, null));
                            }

                        } else {
                            moves.add(new ChessMove(myPosition, newPosition, null));
                        }
                    }
                }

                //check backward right
                if (!rblocked) {
                    if (column + offset > 7) {
                        rblocked = true;
                    } else {

                        newPosition = new ChessPosition(i + 1, column + offset + 1);
                        if (board.getPiece(newPosition) != null) {
                            rblocked = true;
                            if (board.getPiece(newPosition).getTeamColor() != piece.getTeamColor()) {
                                moves.add(new ChessMove(myPosition, newPosition, null));
                            }

                        } else {
                            moves.add(new ChessMove(myPosition, newPosition, null));
                        }
                    }
                }

                offset++;
            }

            //reset variables
            offset = 1;
            rblocked = false;
            lblocked = false;

            //check forward diagonals
            for (int i = row + 1; i <= 7; i++) {

                //check forward left
                if (!lblocked) {
                    if (column - offset < 0) {
                        lblocked = true;
                    } else {

                        newPosition = new ChessPosition(i + 1, column - offset + 1);
                        if (board.getPiece(newPosition) != null) {
                            lblocked = true;
                            if (board.getPiece(newPosition).getTeamColor() != piece.getTeamColor()) {
                                moves.add(new ChessMove(myPosition, newPosition, null));
                            }

                        } else {
                            moves.add(new ChessMove(myPosition, newPosition, null));
                        }
                    }
                }

                //check forward right
                if (!rblocked) {
                    if (column + offset > 7) {
                        rblocked = true;
                    } else {

                        newPosition = new ChessPosition(i + 1, column + offset + 1);
                        if (board.getPiece(newPosition) != null) {
                            rblocked = true;
                            if (board.getPiece(newPosition).getTeamColor() != piece.getTeamColor()) {
                                moves.add(new ChessMove(myPosition, newPosition, null));
                            }

                        } else {
                            moves.add(new ChessMove(myPosition, newPosition, null));
                        }
                    }
                }

                offset++;
            }
        }

        if (piece.getPieceType() == PieceType.KNIGHT) {

            //check forward
            if (row + 2 <= 7) {

                //check forward right
                if (column + 1 <= 7) {

                    newPosition = new ChessPosition(row + 3, column + 2);
                    if (board.getPiece(newPosition) != null) {
                        if (board.getPiece(newPosition).getTeamColor() != piece.getTeamColor()) {
                            moves.add(new ChessMove(myPosition, newPosition, null));
                        }
                    } else {
                        moves.add(new ChessMove(myPosition, newPosition, null));
                    }
                }

                //check forward left
                if (column - 1 >= 0) {

                    newPosition = new ChessPosition(row + 3, column);
                    if (board.getPiece(newPosition) != null) {
                        if (board.getPiece(newPosition).getTeamColor() != piece.getTeamColor()) {
                            moves.add(new ChessMove(myPosition, newPosition, null));
                        }
                    } else {
                        moves.add(new ChessMove(myPosition, newPosition, null));
                    }
                }
            }

            //check backward
            if (row - 2 >= 0) {

                //check backward right
                if (column + 1 <= 7) {

                    newPosition = new ChessPosition(row - 1, column + 2);
                    if (board.getPiece(newPosition) != null) {
                        if (board.getPiece(newPosition).getTeamColor() != piece.getTeamColor()) {
                            moves.add(new ChessMove(myPosition, newPosition, null));
                        }
                    } else {
                        moves.add(new ChessMove(myPosition, newPosition, null));
                    }
                }

                //check backward left
                if (column - 1 >= 0) {

                    newPosition = new ChessPosition(row - 1, column);
                    if (board.getPiece(newPosition) != null) {
                        if (board.getPiece(newPosition).getTeamColor() != piece.getTeamColor()) {
                            moves.add(new ChessMove(myPosition, newPosition, null));
                        }
                    } else {
                        moves.add(new ChessMove(myPosition, newPosition, null));
                    }
                }
            }

            //check right
            if (column + 2 <= 7) {

                //check right forward
                if (row + 1 <= 7) {
                    newPosition = new ChessPosition(row + 2, column + 3);
                    if (board.getPiece(newPosition) != null) {
                        if (board.getPiece(newPosition).getTeamColor() != piece.getTeamColor()) {
                            moves.add(new ChessMove(myPosition, newPosition, null));
                        }
                    } else {
                        moves.add(new ChessMove(myPosition, newPosition, null));
                    }
                }

                //check right backward
                if (row - 1 >= 0) {
                    newPosition = new ChessPosition(row, column + 3);
                    if (board.getPiece(newPosition) != null) {
                        if (board.getPiece(newPosition).getTeamColor() != piece.getTeamColor()) {
                            moves.add(new ChessMove(myPosition, newPosition, null));
                        }
                    } else {
                        moves.add(new ChessMove(myPosition, newPosition, null));
                    }
                }
            }

            //check left
            if (column - 2 >= 0) {

                //check left forward
                if (row + 1 <= 7) {

                    newPosition = new ChessPosition(row + 2, column - 1);
                    if (board.getPiece(newPosition) != null) {
                        if (board.getPiece(newPosition).getTeamColor() != piece.getTeamColor()) {
                            moves.add(new ChessMove(myPosition, newPosition, null));
                        }
                    } else {
                        moves.add(new ChessMove(myPosition, newPosition, null));
                    }
                }

                //check left backward
                if (row - 1 >= 0) {

                    newPosition = new ChessPosition(row, column - 1);
                    if (board.getPiece(newPosition) != null) {
                        if (board.getPiece(newPosition).getTeamColor() != piece.getTeamColor()) {
                            moves.add(new ChessMove(myPosition, newPosition, null));
                        }
                    } else {
                        moves.add(new ChessMove(myPosition, newPosition, null));
                    }
                }
            }
        }
        
        if (piece.getPieceType() == PieceType.PAWN) {
            
            if (piece.getTeamColor() == TeamColor.WHITE) {
                if (row + 1 <= 7) {
                    if (board.getPiece(new ChessPosition(row + 2, column + 1)) == null) {

                        //check white start
                        newPosition = new ChessPosition(row + 3, column + 1);
                        if (row == 1 && board.getPiece(newPosition) == null) {
                            moves.add(new ChessMove(myPosition, newPosition, null));
                        }

                        //check white promotion
                        newPosition = new ChessPosition(row + 2, column + 1);
                        if (row + 1 == 7) {
                            moves.add(new ChessMove(myPosition, newPosition, PieceType.BISHOP));
                            moves.add(new ChessMove(myPosition, newPosition, PieceType.QUEEN));
                            moves.add(new ChessMove(myPosition, newPosition, PieceType.ROOK));
                            moves.add(new ChessMove(myPosition, newPosition, PieceType.KNIGHT));
                        } else {
                            moves.add(new ChessMove(myPosition, newPosition, null));
                        }
                    }
                    
                    //check white right capture
                    if (column + 1 <= 7) {

                        newPosition = new ChessPosition(row + 2, column + 2);
                        if (board.getPiece(newPosition) != null) {
                            if (board.getPiece(newPosition).getTeamColor() != piece.getTeamColor()) {

                                //check white right capture promotion
                                if (row + 1 == 7) {
                                    moves.add(new ChessMove(myPosition, newPosition, PieceType.BISHOP));
                                    moves.add(new ChessMove(myPosition, newPosition, PieceType.QUEEN));
                                    moves.add(new ChessMove(myPosition, newPosition, PieceType.ROOK));
                                    moves.add(new ChessMove(myPosition, newPosition, PieceType.KNIGHT));
                                } else {
                                    moves.add(new ChessMove(myPosition, newPosition, null));
                                }
                            }
                        }
                    }

                    //check white left capture
                    if (column - 1 >= 0) {

                        newPosition = new ChessPosition(row + 2, column);
                        if (board.getPiece(newPosition) != null) {
                            if (board.getPiece(newPosition).getTeamColor() != piece.getTeamColor()) {

                                //check white left capture promotion
                                if (row + 1 == 7) {
                                    moves.add(new ChessMove(myPosition, newPosition, PieceType.BISHOP));
                                    moves.add(new ChessMove(myPosition, newPosition, PieceType.QUEEN));
                                    moves.add(new ChessMove(myPosition, newPosition, PieceType.ROOK));
                                    moves.add(new ChessMove(myPosition, newPosition, PieceType.KNIGHT));
                                } else {
                                    moves.add(new ChessMove(myPosition, newPosition, null));
                                }
                            }
                        }
                    }
                }
            } else {
                if (row - 1 >= 0) {
                    if (board.getPiece(new ChessPosition(row, column + 1)) == null) {

                        //check black start
                        newPosition = new ChessPosition(row - 1, column + 1);
                        if (row == 6 && board.getPiece(newPosition) == null) {
                            moves.add(new ChessMove(myPosition, newPosition, null));
                        }

                        //check black promotion
                        newPosition = new ChessPosition(row, column + 1);
                        if (row - 1 == 0) {
                            moves.add(new ChessMove(myPosition, newPosition, PieceType.BISHOP));
                            moves.add(new ChessMove(myPosition, newPosition, PieceType.QUEEN));
                            moves.add(new ChessMove(myPosition, newPosition, PieceType.ROOK));
                            moves.add(new ChessMove(myPosition, newPosition, PieceType.KNIGHT));
                        } else {
                            moves.add(new ChessMove(myPosition, newPosition, null));
                        }
                    }
                    
                    //check black right capture
                    if (column + 1 <= 7) {

                        newPosition = new ChessPosition(row, column + 2);
                        if (board.getPiece(newPosition) != null) {
                            if (board.getPiece(newPosition).getTeamColor() != piece.getTeamColor()) {

                                //check black right capture promotion
                                if (row - 1 == 0) {
                                    moves.add(new ChessMove(myPosition, newPosition, PieceType.BISHOP));
                                    moves.add(new ChessMove(myPosition, newPosition, PieceType.QUEEN));
                                    moves.add(new ChessMove(myPosition, newPosition, PieceType.ROOK));
                                    moves.add(new ChessMove(myPosition, newPosition, PieceType.KNIGHT));
                                } else {
                                    moves.add(new ChessMove(myPosition, newPosition, null));
                                }
                            }
                        }
                    }

                    //check black left capture
                    if (column - 1 >= 0) {

                        newPosition = new ChessPosition(row, column);
                        if (board.getPiece(newPosition) != null) {

                            //check black left capture promotion
                            if (board.getPiece(newPosition).getTeamColor() != piece.getTeamColor()) {
                                if (row - 1 == 0) {
                                    moves.add(new ChessMove(myPosition, newPosition, PieceType.BISHOP));
                                    moves.add(new ChessMove(myPosition, newPosition, PieceType.QUEEN));
                                    moves.add(new ChessMove(myPosition, newPosition, PieceType.ROOK));
                                    moves.add(new ChessMove(myPosition, newPosition, PieceType.KNIGHT));
                                } else {
                                    moves.add(new ChessMove(myPosition, newPosition, null));
                                }
                            }
                        }
                    }
                }
            }
        }

        return moves;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(pieceColor, type);
    }
}
