package chess;

import java.util.Collection;

import chess.ChessPiece.PieceType;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    public TeamColor team;
    public ChessBoard board;
    public boolean[] castles = {true, true, true, true, true, true};

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        team = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return team;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.team = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {

        //get all possible moves
        Collection<ChessMove> moves = board.getPiece(startPosition).pieceMoves(board, startPosition);
        Collection<ChessMove> realMoves = new ArrayList<ChessMove>();
        ChessPiece piece = board.getPiece(startPosition);

        //check if the king can castle
        if (piece.getPieceType() == PieceType.KING && !isInCheck(piece.getTeamColor())) {

            if (piece.getTeamColor() == TeamColor.WHITE) {

                //check if the white king has moved yet
                if (castles[1] && startPosition.getColumn() == 5 && startPosition.getRow() == 1) {

                    ChessPosition newPosition;
                    ChessPosition interPosition;

                    //check left
                    if (castles[0]) {

                        newPosition = new ChessPosition(1, 3);
                        interPosition = new ChessPosition(1, 4);

                        //make sure the spaces between are clear
                        if (board.getPiece(new ChessPosition(1, 2)) == null && 
                            board.getPiece(newPosition) == null && 
                            board.getPiece(interPosition) == null
                        ) {

                            //verify the legality of intermediate moves
                            if (!checkMove(piece, piece.getTeamColor(), new ChessMove(startPosition, interPosition, null), startPosition)) {

                                //add the king's move
                                moves.add(new ChessMove(startPosition, newPosition, null));
                            } 
                        }
                    }

                    //check right
                    if (castles[2]) {

                        newPosition = new ChessPosition(1, 7);
                        interPosition = new ChessPosition(1, 6);

                        //make sure the spaces between are clear
                        if (board.getPiece(newPosition) == null &&
                            board.getPiece(interPosition) == null
                        ) {

                            //verify the legality of intermediate moves
                            if (!checkMove(piece, piece.getTeamColor(), new ChessMove(startPosition, interPosition, null), startPosition)) {
                                
                                //add the king's move
                                moves.add(new ChessMove(startPosition, newPosition, null));
                            } 
                        }
                    }
                }

            } else {
            
                //check if the black king has moved yet
                if (castles[4] && startPosition.getColumn() == 5 && startPosition.getRow() == 8) {

                    ChessPosition newPosition;
                    ChessPosition interPosition;

                    //check left
                    if (castles[3]) {

                        newPosition = new ChessPosition(8, 3);
                        interPosition = new ChessPosition(8, 4);

                        //make sure the spaces between are clear
                        if (board.getPiece(new ChessPosition(8, 2)) == null && 
                            board.getPiece(newPosition) == null && 
                            board.getPiece(interPosition) == null
                        ) {

                            //verify the legality of intermediate moves
                            if (!checkMove(piece, piece.getTeamColor(), new ChessMove(startPosition, interPosition, null), startPosition)) {
                                
                                //add the king's move
                                moves.add(new ChessMove(startPosition, newPosition, null));
                            } 
                        }
                    }

                    //check right
                    if (castles[5]) {

                        newPosition = new ChessPosition(8, 7);
                        interPosition = new ChessPosition(8, 6);

                        //make sure the spaces between are clear
                        if (board.getPiece(newPosition) == null &&
                            board.getPiece(interPosition) == null
                        ) {

                            //verify the legality of intermediate moves
                            if (!checkMove(piece, piece.getTeamColor(), new ChessMove(startPosition, interPosition, null), startPosition)) {
                                
                                //add the king's move
                                moves.add(new ChessMove(startPosition, newPosition, null));
                            } 
                        }
                    }
                }
            }
        } 

        //check if any of the possible moves leave the king in check
        for (ChessMove move : moves) {
            if (!checkMove(piece, piece.getTeamColor(), move, startPosition)) {
                realMoves.add(move);
            }
        }

        return realMoves;
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {

        //get the piece making the move
        ChessPosition position = move.getStartPosition();
        ChessPiece piece = board.getPiece(position);

        //check if there actually is a piece at that position
        if (piece == null) {
            throw new InvalidMoveException("No Piece");
        }

        //check if a player is moving out of turn
        if (piece.getTeamColor() != getTeamTurn()) {
            throw new InvalidMoveException("Not Your Turn");
        }

        //get a list of valid moves
        Collection<ChessMove> moves = validMoves(position);
        boolean validMove = false;

        //compare the move to each valid move in the list
        for (ChessMove item : moves) {
            if (item.equals(move)) {
                validMove = true;
            }
        }

        if (!validMove) {
            throw new InvalidMoveException("Illegal Move");
        }

        //promote a pawn, if applicable
        if (move.getPromotionPiece() != null) {
            piece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
        }

        //castle a king, if applicable
        if (piece.getPieceType() == PieceType.KING) {
            if (piece.getTeamColor() == TeamColor.WHITE && castles[1]) {

                ChessPiece rook = new ChessPiece(TeamColor.WHITE, PieceType.ROOK);

                //castle to the left
                if (move.getEndPosition().getColumn() == 3) {

                    //move the rook
                    board.addPiece(new ChessPosition(1, 1), null);
                    board.addPiece(new ChessPosition(1, 4), rook);

                    castles[0] = false;

                //castle to the right
                } else if (move.getEndPosition().getColumn() == 7) {

                    //move the rook
                    board.addPiece(new ChessPosition(1, 8), null);
                    board.addPiece(new ChessPosition(1, 6), rook);

                    castles[2] = false;
                }

                castles[1] = false;

            } else if (piece.getTeamColor() == TeamColor.BLACK && castles[4]) {

                ChessPiece rook = new ChessPiece(TeamColor.BLACK, PieceType.ROOK);

                //castle to the left
                if (move.getEndPosition().getColumn() == 3) {

                    //move the rook
                    board.addPiece(new ChessPosition(8, 1), null);
                    board.addPiece(new ChessPosition(8, 4), rook);

                    castles[3] = false;

                //castle to the right
                } else if (move.getEndPosition().getColumn() == 7) {

                    //move the rook
                    board.addPiece(new ChessPosition(8, 8), null);
                    board.addPiece(new ChessPosition(8, 6), rook);

                    castles[5] = false;
                }

                castles[4] = false;
            }
        }

        //register the movement of a rook
        if (piece.getPieceType() == PieceType.ROOK) {
            if (position.getRow() == 1) {

                if (position.getColumn() == 1 && castles[0]) {

                    castles[0] = false;

                } else if (position.getColumn() == 8 && castles[2]) {

                    castles[2] = false;

                }
            } else if (position.getRow() == 8) {

                if (position.getColumn() == 1 && castles[3]) {

                    castles[3] = false;
                
                } else if (position.getColumn() == 8 && castles[5]) {

                    castles[5] = false;
                }
            }
        }

        //update the board to make the move
        board.addPiece(move.getEndPosition(), piece);
        board.addPiece(position, null);

        //switch to the next team's turn
        if (getTeamTurn() == TeamColor.BLACK) {
            setTeamTurn(TeamColor.WHITE);
        } else {
            setTeamTurn(TeamColor.BLACK);
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {

        ChessPosition position;
        ChessPiece king = null;
        ChessPiece piece;
        Collection<ChessMove> enemyMoves = new ArrayList<ChessMove>();

        //find the given team's king and construct a collection of possible enemy moves
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                position = new ChessPosition(i, j);
                piece = board.getPiece(position);
                if (piece != null) {

                    //identifies the king
                    if (piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor) {
                        king = piece;
                    }

                    //adds any possible enemy moves
                    if (piece.getTeamColor() != teamColor) {
                        enemyMoves.addAll(piece.pieceMoves(board, position));
                    }
                }
            }
        }

        //checks enemy moves to see if they target the king
        for (ChessMove move : enemyMoves) {
            if (board.getPiece(move.getEndPosition()) == king) {
                return true;
            }
        }

        return false;


    }

    /**
     * Simulates a move being made to see if it will result in a check
     * 
     * @param piece the piece making the move
     * @param teamColor the color of the team being checked
     * @param move the move being simulated
     * @param position the position of the piece making the move
     * @return whether the move will result in a check
     */
    public boolean checkMove(ChessPiece piece, ChessGame.TeamColor teamColor, ChessMove move, ChessPosition position) {

        //store the piece currently at the end position
        ChessPiece destination = board.getPiece(move.getEndPosition());

        //see if the piece can actually move there
        if (destination == null || destination.getTeamColor() != teamColor) {
        
            //add the piece to the end position and replace its previous position with a null value
            board.addPiece(move.getEndPosition(), piece);
            board.addPiece(position, null);

            //see if the current board state is in check, then set the board back to its previous configuration
            if (!isInCheck(teamColor)) {
                board.addPiece(position, piece);
                board.addPiece(move.getEndPosition(), destination);
                
                return false;
            }
            
            board.addPiece(position, piece);
            board.addPiece(move.getEndPosition(), destination);
        }

        return true;
    }

    /**
     * Determines if there is a possible move a given team can make that will not result in a check
     * 
     * @param teamColor the color of the team
     * @return whether the team is able to make a move that will not result in a check
     */
    public boolean canAvoidCheck(ChessGame.TeamColor teamColor) {

        ChessPosition position;
        ChessPiece piece;
        Collection<ChessMove> moves;

        //find all allied pieces
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {

                position = new ChessPosition(i, j);
                piece = board.getPiece(position);

                if (piece != null) {

                    //get the moves of the piece
                    if (piece.getTeamColor() == teamColor) {
                        moves = validMoves(position);
                        boolean stillInCheck;

                        //see if any possible moves get the king out of check
                        for (ChessMove move : moves) {
                            stillInCheck = checkMove(piece, teamColor, move, position);

                            if (!stillInCheck) {
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {

        //if the king is in check, see if they can escape it
        if (isInCheck(teamColor)) {
            return !canAvoidCheck(teamColor);
        }

        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {

        //if the king is not in check, see if they can avoid it
        if (!isInCheck(teamColor)) {
            return !canAvoidCheck(teamColor);
        }

        return false;
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((team == null) ? 0 : team.hashCode());
        result = prime * result + ((board == null) ? 0 : board.hashCode());
        result = prime * result + Arrays.hashCode(castles);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ChessGame other = (ChessGame) obj;
        if (team != other.team)
            return false;
        if (board == null) {
            if (other.board != null)
                return false;
        } else if (!board.equals(other.board))
            return false;
        if (!Arrays.equals(castles, other.castles))
            return false;
        return true;
    }
}
