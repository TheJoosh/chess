package chess;

import java.util.Collection;
import java.util.List;
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

        if (piece.getPieceType() == PieceType.QUEEN) {
            
            int offset = 1;
            boolean rblocked = false;
            boolean lblocked = false;

            for (int i = row - 1; i >= 0; i--) {
                if (!lblocked){
                    if (column - offset < 0) {
                        lblocked = true;
                    } else {
                        if (board.getPiece(new ChessPosition(i + 1, column - offset + 1)) != null) {
                            lblocked = true;
                            if (board.getPiece(new ChessPosition(i + 1, column - offset + 1)).getTeamColor() != piece.getTeamColor()) {
                                moves.add(new ChessMove(myPosition, new ChessPosition(i + 1, column - offset + 1), null));
                            }

                        } else {
                            moves.add(new ChessMove(myPosition, new ChessPosition(i + 1, column - offset + 1), null));
                        }
                    }
                }

                if (!rblocked) {
                    if (column + offset > 7) {
                        rblocked = true;
                    } else {
                        if (board.getPiece(new ChessPosition(i + 1, column + offset + 1)) != null) {
                            rblocked = true;
                            if (board.getPiece(new ChessPosition(i + 1, column + offset + 1)).getTeamColor() != piece.getTeamColor()) {
                                moves.add(new ChessMove(myPosition, new ChessPosition(i + 1, column + offset + 1), null));
                            }

                        } else {
                            moves.add(new ChessMove(myPosition, new ChessPosition(i + 1, column + offset + 1), null));
                        }
                    }
                }

                offset++;
            }

            offset = 1;
            rblocked = false;
            lblocked = false;

            for (int i = myPosition.getRow(); i <= 7; i++) {
                if (!lblocked) {
                    if (column - offset < 0) {
                        lblocked = true;
                    } else {
                        if (board.getPiece(new ChessPosition(i + 1, column - offset + 1)) != null) {
                            lblocked = true;
                            if (board.getPiece(new ChessPosition(i + 1, column - offset + 1)).getTeamColor() != piece.getTeamColor()) {
                                moves.add(new ChessMove(myPosition, new ChessPosition(i + 1, column - offset + 1), null));
                            }

                        } else {
                            moves.add(new ChessMove(myPosition, new ChessPosition(i + 1, column - offset + 1), null));
                        }
                    }
                }

                if (!rblocked) {
                    if (column + offset > 7) {
                        rblocked = true;
                    } else {
                        if (board.getPiece(new ChessPosition(i + 1, column + offset + 1)) != null) {
                            rblocked = true;
                            if (board.getPiece(new ChessPosition(i + 1, column + offset + 1)).getTeamColor() != piece.getTeamColor()) {
                                moves.add(new ChessMove(myPosition, new ChessPosition(i + 1, column + offset + 1), null));
                            }

                        } else {
                            moves.add(new ChessMove(myPosition, new ChessPosition(i + 1, column + offset + 1), null));
                        }
                    }
                }

                offset++;
            }

            for (int i = row + 1; i <= 7; i++) {
                if (board.getPiece(new ChessPosition(i + 1, column + 1)) != null) {
                    if (board.getPiece(new ChessPosition(i + 1, column + 1)).getTeamColor() != piece.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(i + 1, column + 1), null));
                    }
                    break;
                } else {
                    moves.add(new ChessMove(myPosition, new ChessPosition(i + 1, column + 1), null));
                }
            }
            
            for (int i = row - 1; i >= 0; i--) {
                if (board.getPiece(new ChessPosition(i + 1, column + 1)) != null) {
                    if (board.getPiece(new ChessPosition(i + 1, column + 1)).getTeamColor() != piece.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(i + 1, column + 1), null));
                    }
                    break;
                } else {
                    moves.add(new ChessMove(myPosition, new ChessPosition(i + 1, column + 1), null));
                }
            }

            for (int i = column - 1; i >= 0; i--) {
                if (board.getPiece(new ChessPosition(row + 1, i + 1)) != null) {
                    if (board.getPiece(new ChessPosition(row + 1, i + 1)).getTeamColor() != piece.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(row + 1, i + 1), null));
                    }
                    break;
                } else {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row + 1, i + 1), null));
                }
            }

            for (int i = column + 1; i <= 7; i++) {
                if (board.getPiece(new ChessPosition(row + 1, i + 1)) != null) {
                    if (board.getPiece(new ChessPosition(row + 1, i + 1)).getTeamColor() != piece.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(row + 1, i + 1), null));
                    }
                    break;
                } else {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row + 1, i + 1), null));
                }
            }
        }

        if (piece.getPieceType() == PieceType.ROOK) {
            for (int i = row + 1; i <= 7; i++) {
                if (board.getPiece(new ChessPosition(i + 1, column + 1)) != null) {
                    if (board.getPiece(new ChessPosition(i + 1, column + 1)).getTeamColor() != piece.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(i + 1, column + 1), null));
                    }
                    break;
                } else {
                    moves.add(new ChessMove(myPosition, new ChessPosition(i + 1, column + 1), null));
                }
            }
            
            for (int i = row - 1; i >= 0; i--) {
                if (board.getPiece(new ChessPosition(i + 1, column + 1)) != null) {
                    if (board.getPiece(new ChessPosition(i + 1, column + 1)).getTeamColor() != piece.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(i + 1, column + 1), null));
                    }
                    break;
                } else {
                    moves.add(new ChessMove(myPosition, new ChessPosition(i + 1, column + 1), null));
                }
            }

            for (int i = column - 1; i >= 0; i--) {
                if (board.getPiece(new ChessPosition(row + 1, i + 1)) != null) {
                    if (board.getPiece(new ChessPosition(row + 1, i + 1)).getTeamColor() != piece.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(row + 1, i + 1), null));
                    }
                    break;
                } else {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row + 1, i + 1), null));
                }
            }

            for (int i = column + 1; i <= 7; i++) {
                if (board.getPiece(new ChessPosition(row + 1, i + 1)) != null) {
                    if (board.getPiece(new ChessPosition(row + 1, i + 1)).getTeamColor() != piece.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(row + 1, i + 1), null));
                    }
                    break;
                } else {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row + 1, i + 1), null));
                }
            }
        }

        if (piece.getPieceType() == PieceType.BISHOP) {

            int offset = 1;
            boolean rblocked = false;
            boolean lblocked = false;

            for (int i = row - 1; i >= 0; i--) {
                if (!lblocked){
                    if (column - offset < 0) {
                        lblocked = true;
                    } else {
                        if (board.getPiece(new ChessPosition(i + 1, column - offset + 1)) != null) {
                            lblocked = true;
                            if (board.getPiece(new ChessPosition(i + 1, column - offset + 1)).getTeamColor() != piece.getTeamColor()) {
                                moves.add(new ChessMove(myPosition, new ChessPosition(i + 1, column - offset + 1), null));
                            }

                        } else {
                            moves.add(new ChessMove(myPosition, new ChessPosition(i + 1, column - offset + 1), null));
                        }
                    }
                }

                if (!rblocked) {
                    if (column + offset > 7) {
                        rblocked = true;
                    } else {
                        if (board.getPiece(new ChessPosition(i + 1, column + offset + 1)) != null) {
                            rblocked = true;
                            if (board.getPiece(new ChessPosition(i + 1, column + offset + 1)).getTeamColor() != piece.getTeamColor()) {
                                moves.add(new ChessMove(myPosition, new ChessPosition(i + 1, column + offset + 1), null));
                            }

                        } else {
                            moves.add(new ChessMove(myPosition, new ChessPosition(i + 1, column + offset + 1), null));
                        }
                    }
                }

                offset++;
            }

            offset = 1;
            rblocked = false;
            lblocked = false;

            for (int i = myPosition.getRow(); i <= 7; i++) {
                if (!lblocked) {
                    if (column - offset < 0) {
                        lblocked = true;
                    } else {
                        if (board.getPiece(new ChessPosition(i + 1, column - offset + 1)) != null) {
                            lblocked = true;
                            if (board.getPiece(new ChessPosition(i + 1, column - offset + 1)).getTeamColor() != piece.getTeamColor()) {
                                moves.add(new ChessMove(myPosition, new ChessPosition(i + 1, column - offset + 1), null));
                            }

                        } else {
                            moves.add(new ChessMove(myPosition, new ChessPosition(i + 1, column - offset + 1), null));
                        }
                    }
                }

                if (!rblocked) {
                    if (column + offset > 7) {
                        rblocked = true;
                    } else {
                        if (board.getPiece(new ChessPosition(i + 1, column + offset + 1)) != null) {
                            rblocked = true;
                            if (board.getPiece(new ChessPosition(i + 1, column + offset + 1)).getTeamColor() != piece.getTeamColor()) {
                                moves.add(new ChessMove(myPosition, new ChessPosition(i + 1, column + offset + 1), null));
                            }

                        } else {
                            moves.add(new ChessMove(myPosition, new ChessPosition(i + 1, column + offset + 1), null));
                        }
                    }
                }

                offset++;
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
