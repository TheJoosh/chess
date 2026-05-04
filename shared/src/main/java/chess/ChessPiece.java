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

        if (piece.getPieceType() == PieceType.KING) {

            if (row + 1 <= 7) {

                //check forward
                if (board.getPiece(new ChessPosition(row + 2, column + 1)) != null) {
                    if (board.getPiece(new ChessPosition(row + 2, column + 1)).getTeamColor() != piece.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(row + 2, column + 1), null));
                    }
                } else {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row + 2, column + 1), null));
                }

                //check forward right
                if (column + 1 <= 7) {
                    if (board.getPiece(new ChessPosition(row + 2, column + 2)) != null) {
                        if (board.getPiece(new ChessPosition(row + 2, column + 2)).getTeamColor() != piece.getTeamColor()) {
                            moves.add(new ChessMove(myPosition, new ChessPosition(row + 2, column + 2), null));
                        }
                    } else {
                        moves.add(new ChessMove(myPosition, new ChessPosition(row + 2, column + 2), null));
                    }
                }

                //check forward left
                if (column - 1 >= 0) {
                    if (board.getPiece(new ChessPosition(row + 2, column)) != null) {
                        if (board.getPiece(new ChessPosition(row + 2, column)).getTeamColor() != piece.getTeamColor()) {
                            moves.add(new ChessMove(myPosition, new ChessPosition(row + 2, column), null));
                        }
                    } else {
                        moves.add(new ChessMove(myPosition, new ChessPosition(row + 2, column), null));
                    }
                }
            }
            if (row - 1 >= 0) {

                //check backward 
                if (board.getPiece(new ChessPosition(row, column + 1)) != null) {
                    if (board.getPiece(new ChessPosition(row, column + 1)).getTeamColor() != piece.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(row, column + 1), null));
                    }
                } else {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, column + 1), null));
                }

                //check backward right
                if (column + 1 <= 7) {
                    if (board.getPiece(new ChessPosition(row, column + 2)) != null) {
                        if (board.getPiece(new ChessPosition(row, column + 2)).getTeamColor() != piece.getTeamColor()) {
                            moves.add(new ChessMove(myPosition, new ChessPosition(row, column + 2), null));
                        }
                    } else {
                        moves.add(new ChessMove(myPosition, new ChessPosition(row, column + 2), null));
                    }
                }

                //check backward left
                if (column - 1 >= 0) {
                    if (board.getPiece(new ChessPosition(row, column)) != null) {
                        if (board.getPiece(new ChessPosition(row, column)).getTeamColor() != piece.getTeamColor()) {
                            moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
                        }
                    } else {
                        moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
                    }
                }
            }

            //check right
            if (column + 1 <= 7) {
                if (board.getPiece(new ChessPosition(row + 1, column + 2)) != null) {
                        if (board.getPiece(new ChessPosition(row + 1, column + 2)).getTeamColor() != piece.getTeamColor()) {
                            moves.add(new ChessMove(myPosition, new ChessPosition(row + 1, column + 2), null));
                        }
                    } else {
                        moves.add(new ChessMove(myPosition, new ChessPosition(row + 1, column + 2), null));
                    }
            }

            //check left
            if (column - 1 >= 0) {
                if (board.getPiece(new ChessPosition(row + 1, column)) != null) {
                        if (board.getPiece(new ChessPosition(row + 1, column)).getTeamColor() != piece.getTeamColor()) {
                            moves.add(new ChessMove(myPosition, new ChessPosition(row + 1, column), null));
                        }
                    } else {
                        moves.add(new ChessMove(myPosition, new ChessPosition(row + 1, column), null));
                    }
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

                //check backward right
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

                //check forward right
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

            //check forward
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
            
            //check backward
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

            //check left
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

            //check right
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

            //check forward
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
            
            //check backward
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

            //check left
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

            //check right
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

            //check backward diagonals
            for (int i = row - 1; i >= 0; i--) {

                //check backward left
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

                //check backward right
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

                //check forward right
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

        if (piece.getPieceType() == PieceType.KNIGHT) {

            //check forward
            if (row + 2 <= 7) {

                //check forward right
                if (column + 1 <= 7) {
                    if (board.getPiece(new ChessPosition(row + 3, column + 2)) != null) {
                        if (board.getPiece(new ChessPosition(row + 3, column + 2)).getTeamColor() != piece.getTeamColor()) {
                            moves.add(new ChessMove(myPosition, new ChessPosition(row + 3, column + 2), null));
                        }
                    } else {
                        moves.add(new ChessMove(myPosition, new ChessPosition(row + 3, column + 2), null));
                    }
                }

                //check forward left
                if (column - 1 >= 0) {
                    if (board.getPiece(new ChessPosition(row + 3, column)) != null) {
                        if (board.getPiece(new ChessPosition(row + 3, column)).getTeamColor() != piece.getTeamColor()) {
                            moves.add(new ChessMove(myPosition, new ChessPosition(row + 3, column), null));
                        }
                    } else {
                        moves.add(new ChessMove(myPosition, new ChessPosition(row + 3, column), null));
                    }
                }
            }

            //check backward
            if (row - 2 >= 0) {

                //check backward right
                if (column + 1 <= 7) {
                    if (board.getPiece(new ChessPosition(row - 1, column + 2)) != null) {
                        if (board.getPiece(new ChessPosition(row - 1, column + 2)).getTeamColor() != piece.getTeamColor()) {
                            moves.add(new ChessMove(myPosition, new ChessPosition(row - 1, column + 2), null));
                        }
                    } else {
                        moves.add(new ChessMove(myPosition, new ChessPosition(row - 1, column + 2), null));
                    }
                }

                //check backward left
                if (column - 1 >= 0) {
                    if (board.getPiece(new ChessPosition(row - 1, column)) != null) {
                        if (board.getPiece(new ChessPosition(row - 1, column)).getTeamColor() != piece.getTeamColor()) {
                            moves.add(new ChessMove(myPosition, new ChessPosition(row - 1, column), null));
                        }
                    } else {
                        moves.add(new ChessMove(myPosition, new ChessPosition(row - 1, column), null));
                    }
                }
            }

            //check right
            if (column + 2 <= 7) {

                //check right forward
                if (row + 1 <= 7) {
                    if (board.getPiece(new ChessPosition(row + 2, column + 3)) != null) {
                        if (board.getPiece(new ChessPosition(row + 2, column + 3)).getTeamColor() != piece.getTeamColor()) {
                            moves.add(new ChessMove(myPosition, new ChessPosition(row + 2, column + 3), null));
                        }
                    } else {
                        moves.add(new ChessMove(myPosition, new ChessPosition(row + 2, column + 3), null));
                    }
                }

                //check right backward
                if (row - 1 >= 0) {
                    if (board.getPiece(new ChessPosition(row, column + 3)) != null) {
                        if (board.getPiece(new ChessPosition(row, column + 3)).getTeamColor() != piece.getTeamColor()) {
                            moves.add(new ChessMove(myPosition, new ChessPosition(row, column + 3), null));
                        }
                    } else {
                        moves.add(new ChessMove(myPosition, new ChessPosition(row, column + 3), null));
                    }
                }
            }

            //check left
            if (column - 2 >= 0) {

                //check left forward
                if (row + 1 <= 7) {
                    if (board.getPiece(new ChessPosition(row + 2, column - 1)) != null) {
                        if (board.getPiece(new ChessPosition(row + 2, column - 1)).getTeamColor() != piece.getTeamColor()) {
                            moves.add(new ChessMove(myPosition, new ChessPosition(row + 2, column - 1), null));
                        }
                    } else {
                        moves.add(new ChessMove(myPosition, new ChessPosition(row + 2, column - 1), null));
                    }
                }

                //check left backward
                if (row - 1 >= 0) {
                    if (board.getPiece(new ChessPosition(row, column - 1)) != null) {
                        if (board.getPiece(new ChessPosition(row, column - 1)).getTeamColor() != piece.getTeamColor()) {
                            moves.add(new ChessMove(myPosition, new ChessPosition(row, column - 1), null));
                        }
                    } else {
                        moves.add(new ChessMove(myPosition, new ChessPosition(row, column - 1), null));
                    }
                }
            }
        }
        
        if (piece.getPieceType() == PieceType.PAWN) {
            
            if (piece.getTeamColor() == TeamColor.WHITE) {
                if (row + 1 <= 7) {
                    if (board.getPiece(new ChessPosition(row + 2, column + 1)) == null) {

                        //check white start
                        if (row == 1 && board.getPiece(new ChessPosition(row + 3, column + 1)) == null) {
                            moves.add(new ChessMove(myPosition, new ChessPosition(row + 3, column + 1), null));
                        }

                        //check white promotion
                        if (row + 1 == 7) {
                            moves.add(new ChessMove(myPosition, new ChessPosition(row + 2, column + 1), PieceType.BISHOP));
                            moves.add(new ChessMove(myPosition, new ChessPosition(row + 2, column + 1), PieceType.QUEEN));
                            moves.add(new ChessMove(myPosition, new ChessPosition(row + 2, column + 1), PieceType.ROOK));
                            moves.add(new ChessMove(myPosition, new ChessPosition(row + 2, column + 1), PieceType.KNIGHT));
                        } else {
                            moves.add(new ChessMove(myPosition, new ChessPosition(row + 2, column + 1), null));
                        }
                    }
                    
                    //check white right capture
                    if (column + 1 <= 7) {
                        if (board.getPiece(new ChessPosition(row + 2, column + 2)) != null) {
                            if (board.getPiece(new ChessPosition(row + 2, column + 2)).getTeamColor() != piece.getTeamColor()) {

                                //check white right capture promotion
                                if (row + 1 == 7) {
                                    moves.add(new ChessMove(myPosition, new ChessPosition(row + 2, column + 2), PieceType.BISHOP));
                                    moves.add(new ChessMove(myPosition, new ChessPosition(row + 2, column + 2), PieceType.QUEEN));
                                    moves.add(new ChessMove(myPosition, new ChessPosition(row + 2, column + 2), PieceType.ROOK));
                                    moves.add(new ChessMove(myPosition, new ChessPosition(row + 2, column + 2), PieceType.KNIGHT));
                                } else {
                                    moves.add(new ChessMove(myPosition, new ChessPosition(row + 2, column + 2), null));
                                }
                            }
                        }
                    }

                    //check white left capture
                    if (column - 1 >= 0) {
                        if (board.getPiece(new ChessPosition(row + 2, column)) != null) {
                            if (board.getPiece(new ChessPosition(row + 2, column)).getTeamColor() != piece.getTeamColor()) {

                                //check white left capture promotion
                                if (row + 1 == 7) {
                                    moves.add(new ChessMove(myPosition, new ChessPosition(row + 2, column), PieceType.BISHOP));
                                    moves.add(new ChessMove(myPosition, new ChessPosition(row + 2, column), PieceType.QUEEN));
                                    moves.add(new ChessMove(myPosition, new ChessPosition(row + 2, column), PieceType.ROOK));
                                    moves.add(new ChessMove(myPosition, new ChessPosition(row + 2, column), PieceType.KNIGHT));
                                } else {
                                    moves.add(new ChessMove(myPosition, new ChessPosition(row + 2, column), null));
                                }
                            }
                        }
                    }
                }
            } else {
                if (row - 1 >= 0) {
                    if (board.getPiece(new ChessPosition(row, column + 1)) == null) {

                        //check black start
                        if (row == 6 && board.getPiece(new ChessPosition(row - 1, column + 1)) == null) {
                            moves.add(new ChessMove(myPosition, new ChessPosition(row - 1, column + 1), null));
                        }

                        //check black promotion
                        if (row - 1 == 0) {
                            moves.add(new ChessMove(myPosition, new ChessPosition(row, column + 1), PieceType.BISHOP));
                            moves.add(new ChessMove(myPosition, new ChessPosition(row, column + 1), PieceType.QUEEN));
                            moves.add(new ChessMove(myPosition, new ChessPosition(row, column + 1), PieceType.ROOK));
                            moves.add(new ChessMove(myPosition, new ChessPosition(row, column + 1), PieceType.KNIGHT));
                        } else {
                            moves.add(new ChessMove(myPosition, new ChessPosition(row, column + 1), null));
                        }
                    }
                    
                    //check black right capture
                    if (column + 1 <= 7) {
                        if (board.getPiece(new ChessPosition(row, column + 2)) != null) {
                            if (board.getPiece(new ChessPosition(row, column + 2)).getTeamColor() != piece.getTeamColor()) {

                                //check black right capture promotion
                                if (row - 1 == 0) {
                                    moves.add(new ChessMove(myPosition, new ChessPosition(row, column + 2), PieceType.BISHOP));
                                    moves.add(new ChessMove(myPosition, new ChessPosition(row, column + 2), PieceType.QUEEN));
                                    moves.add(new ChessMove(myPosition, new ChessPosition(row, column + 2), PieceType.ROOK));
                                    moves.add(new ChessMove(myPosition, new ChessPosition(row, column + 2), PieceType.KNIGHT));
                                } else {
                                    moves.add(new ChessMove(myPosition, new ChessPosition(row, column + 2), null));
                                }
                            }
                        }
                    }

                    //check black left capture
                    if (column - 1 >= 0) {
                        if (board.getPiece(new ChessPosition(row, column)) != null) {

                            //check black left capture promotion
                            if (board.getPiece(new ChessPosition(row, column)).getTeamColor() != piece.getTeamColor()) {
                                if (row - 1 == 0) {
                                    moves.add(new ChessMove(myPosition, new ChessPosition(row, column), PieceType.BISHOP));
                                    moves.add(new ChessMove(myPosition, new ChessPosition(row, column), PieceType.QUEEN));
                                    moves.add(new ChessMove(myPosition, new ChessPosition(row, column), PieceType.ROOK));
                                    moves.add(new ChessMove(myPosition, new ChessPosition(row, column), PieceType.KNIGHT));
                                } else {
                                    moves.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
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
