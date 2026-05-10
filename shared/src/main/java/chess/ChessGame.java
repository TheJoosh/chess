package chess;
import java.util.Collection;
import chess.ChessPiece.PieceType;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A class that can manage a chess game, making moves on a board
 */
public class ChessGame {

    public TeamColor team;
    public ChessBoard board;
    public boolean[] castles = {true, true, true, true, true, true};
    public ChessPosition enPassant;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        team = TeamColor.WHITE;
        enPassant = null;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return team;
    }

    /**
     * Sets which teams turn it is
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
     * Checks if a particular castling move is possible and adds it to the list
     *
     */
    public void castle(ChessPiece king, ChessPosition startPosition, Collection<ChessMove> moves, int direction) {

        int end;
        if (direction == 1) {
            end = 1;
        } else {
            end = 8;
        }
        ChessPosition newPosition = new ChessPosition(startPosition.getRow(), startPosition.getColumn() + (2 * direction));
        ChessPosition interPosition = new ChessPosition(startPosition.getRow(), startPosition.getColumn() + direction);
        ChessPosition thirdPosition = new ChessPosition(startPosition.getRow(), end + (3 * direction));

        //make sure the spaces between are clear
        if (board.getPiece(newPosition) == null &&
            board.getPiece(interPosition) == null &&
            (board.getPiece(thirdPosition) == null || board.getPiece(thirdPosition) == king)
        ) {
            //verify the legality of intermediate moves
            if (!checkMove(king, king.getTeamColor(), new ChessMove(startPosition, interPosition, null), startPosition)) {
                
                //add the king's move
                moves.add(new ChessMove(startPosition, newPosition, null));
            } 
        }
    }

    /**
     * Checks if a king can castle in either direction
     */
    public void checkCastle(ChessPosition startPosition, ChessPiece king, Collection<ChessMove> moves, TeamColor color) {

        int kingIndex;
        int start;
        if (color == TeamColor.WHITE) {
            kingIndex = 1;
            start = 1;
        } else {
            kingIndex = 4;
            start = 8;
        }
        int lRookIndex = kingIndex - 1;
        int rRookIndex = kingIndex + 1;

        if (castles[kingIndex] && startPosition.getColumn() == 5 && startPosition.getRow() == start) {

            //check left
            if (castles[lRookIndex]) {
                castle(king, startPosition, moves, -1);
            }

            //check right
            if (castles[rRookIndex]) {
                castle(king, startPosition, moves, 1);
            }
        }
    }

    /**
     * Moves the rooks during a castle
     */
    public void executeCastle(ChessMove move, TeamColor teamColor, int direction) throws InvalidMoveException {

        //ensure the move is legal
        int kingIndex;
        int start;
        if (teamColor == TeamColor.WHITE) {
            kingIndex = 1;
            start = 1;
        } else {
            kingIndex = 4;
            start = 8;
        }
        int rookIndex = kingIndex + direction;
        if(!castles[kingIndex] || !castles[rookIndex]) {
            throw new InvalidMoveException("Illegal Move");    
        }
        int end;
        int offset;
        if (direction == 1) {
            end = 8;
            offset = 2;
        } else {
            end = 1;
            offset = 3;
        }

        //move the rook
        board.addPiece(new ChessPosition(start, end), null);
        board.addPiece(new ChessPosition(start, end - (direction * offset)), new ChessPiece(teamColor, PieceType.ROOK));
        castles[rookIndex] = false;
        castles[kingIndex] = false;
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @return Set of valid moves for requested piece, or null if no piece at
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {

        //get all possible moves
        Collection<ChessMove> moves = board.getPiece(startPosition).pieceMoves(board, startPosition);
        Collection<ChessMove> realMoves = new ArrayList<ChessMove>();
        ChessPiece piece = board.getPiece(startPosition);

        //check if the king can castle
        if (piece.getPieceType() == PieceType.KING && !isInCheck(piece.getTeamColor())) {
            checkCastle(startPosition, piece, moves, piece.getTeamColor());
        } 

        //check if a pawn can capture en passant
        if (piece.getPieceType() == PieceType.PAWN && enPassant != null) {

            int direction;
            if (piece.getTeamColor() == TeamColor.WHITE) {
                direction = 1;
            } else {
                direction = -1;
            }
            if (enPassant.getRow() == startPosition.getRow() + direction && 
                (enPassant.getColumn() == startPosition.getColumn() + 1 || 
                enPassant.getColumn() == startPosition.getColumn() -1)
            ) {
                moves.add(new ChessMove(startPosition, enPassant, null));
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

        //check if a player is moving out of turn
        if (piece == null || piece.getTeamColor() != getTeamTurn()) {
            throw new InvalidMoveException("Illegal Move");
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

        //activate en passant, if applicable
        if (piece.getPieceType() == PieceType.PAWN) {
            int start;
            int direction;
            if (piece.getTeamColor() == TeamColor.WHITE) {
                start = 2;
                direction = 1;
            } else {
                start = 7;
                direction = -1;
            }

            if (enPassant != null) {

                //capture en passant
                if (enPassant.equals(move.getEndPosition())) {
                    board.addPiece(new ChessPosition(enPassant.getRow() - direction, enPassant.getColumn()), null);
                }

                //reset en passant
                enPassant = null;
            }

            //set en passant to the position that the pawn skipped
            if (position.getRow() == start && move.getEndPosition().getRow() != start + direction) {
                enPassant = new ChessPosition(start + direction, position.getColumn());
            } 
        }

        //castle a king, if applicable
        if (piece.getPieceType() == PieceType.KING && position.getColumn() == 5) {
            if (castles[1] || castles[4]) {
                if (move.getEndPosition().getColumn() == 7 || move.getEndPosition().getColumn() == 3) {

                    //determine the direction
                    int direction = 1;
                    if (move.getEndPosition().getColumn() == 3) {
                        direction = -1;
                    }

                    //move the rook
                    executeCastle(move, piece.getTeamColor(), direction);
                }
            }

            //register the movement of a king
            if (position.getRow() == 1) {
                castles[1] = false;
            } else if (position.getRow() == 8) {
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

                //get the moves of the piece
                if (piece != null && piece.getTeamColor() == teamColor) {
                    moves = validMoves(position);
                    boolean stillInCheck;

                    //see if any possible moves get the king out of check
                    for (ChessMove move : moves) {
                        stillInCheck = checkMove(piece, teamColor, move, position);
                        return !stillInCheck;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
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
        result = prime * result + ((enPassant == null) ? 0 : enPassant.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true; }
        if (obj == null)  {
            return false; }
        if (getClass() != obj.getClass()) {
            return false; }
        ChessGame other = (ChessGame) obj;
        if (team != other.team) {
            return false; }
        if (board == null) {
            if (other.board != null) {
                return false; }
        } else if (!board.equals(other.board)) {
            return false; }
        if (!Arrays.equals(castles, other.castles)) {
            return false; }
        if (enPassant == null) {
            if (other.enPassant != null) {
                return false; }
        } else if (!enPassant.equals(other.enPassant)) {
            return false; }
        return true;
    }
}