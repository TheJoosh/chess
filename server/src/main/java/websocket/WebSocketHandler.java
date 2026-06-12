package websocket;

import com.google.gson.Gson;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.*;
import websocket.messages.*;
import websocket.messages.ServerMessage.ServerMessageType;
import service.*;
import model.GameList;
import model.GameData;
import dataaccess.SQLDataAccess;
import dataaccess.SQLAuthAccess;
import dataaccess.SQLGameAccess;
import dataaccess.SQLUserAccess;
import dataaccess.DataAccessException;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final SQLDataAccess dataAccess = new SQLDataAccess(new SQLGameAccess(), new SQLUserAccess(), new SQLAuthAccess());
    private final GameService gameService = new GameService(dataAccess);
    private final AuthService authService = new AuthService(dataAccess);

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
        try {
            UserGameCommand action = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            switch (action.getCommandType()) {
                case CONNECT -> enter(action.getUsername(), action.getAuthToken(), action.getTeam(), action.getGameID(), ctx.session);
                case LEAVE -> exit(action.getUsername(), action.getAuthToken(), action.getTeam(), action.getGameID(), ctx.session);
                case MAKE_MOVE -> move(
                                    action.getUsername(), 
                                    action.getAuthToken(), 
                                    action.getMove(), 
                                    action.getTeam(),
                                    action.getOtherTeam(),
                                    action.getGameID(), 
                                    ctx.session
                                );
                case RESIGN -> resign(action.getUsername(), action.getAuthToken(), action.getGameID(), ctx.session);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed\n");
    }

    private void notify(int id, Session session, ServerMessage message) throws IOException {
        connections.broadcast(id, session, message);
    }

    private void move(String username, String auth, ChessMove move, ChessGame.TeamColor team,
                    ChessGame.TeamColor otherTeam,int id, Session session) throws IOException {
        connections.add(id, session);
        var message = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        var broadcast = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        if (!authenticated(auth)) {
            message = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            message.setErrorMessage("Unauthorized");
            connections.send(id, session, message);
            return;}
        try {
            username = authService.getUsername(auth);
        } catch (Exception e) {
            message = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            message.setErrorMessage("Unauthorized");
            connections.send(id, session, message);
            return;}
        GameList games;
        try {games = gameService.listGames();
        } catch (Exception e) {
            message = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            message.setErrorMessage(e.getMessage());
            connections.send(id, session, message);
            return;}

        if (!games.containsKey(id)) {
            message = new ServerMessage(ServerMessageType.ERROR);
            message.setErrorMessage("Invalid game ID");
            connections.send(id, session, message);
            return;}
        GameData gameData = games.get(id);
        ChessGame game = gameData.game();

        if (gameData.whiteUsername().equals("GAME") && gameData.blackUsername().equals("OVER")) {
            message = new ServerMessage(ServerMessageType.ERROR);
            message.setErrorMessage("Game is over");
            connections.send(id, session, message);
            return;}
        if (gameData.blackUsername() != null && gameData.blackUsername().equals(username)) {
            team = ChessGame.TeamColor.BLACK;
        } else if (gameData.whiteUsername() != null && gameData.whiteUsername().equals(username)) {
            team = ChessGame.TeamColor.WHITE;
        } else {
            message = new ServerMessage(ServerMessageType.ERROR);
            message.setErrorMessage("Observers cannot move");
            connections.send(id, session, message);
            return;}

        ChessPiece chessPiece = game.board.getPiece(move.getStartPosition());
        if (chessPiece.getTeamColor() != team) {
            message = new ServerMessage(ServerMessageType.ERROR);
            message.setErrorMessage("Cannot move opponent's pieces");
            connections.send(id, session, message);
            return;}
        
        String start = parsePosition(move.getStartPosition().toString());
        String end = parsePosition(move.getEndPosition().toString());
        String piece = null;
        String notification = "";

        try {game.makeMove(move);
            gameService.updateGame(id, games.get(id).whiteUsername(), games.get(id).blackUsername(), game);
        } catch (Exception e) {
            message = new ServerMessage(ServerMessageType.ERROR);
            message.setErrorMessage(e.getMessage());
            connections.send(id, session, message);
            return;}

        if (team == ChessGame.TeamColor.BLACK) { otherTeam = ChessGame.TeamColor.WHITE;} 
        else {otherTeam = ChessGame.TeamColor.BLACK;}

        boolean check = game.isInCheck(otherTeam);
        boolean checkmate = game.isInCheckmate(otherTeam);
        boolean stalemate = game.isInStalemate(otherTeam);

        if (move.getPromotionPiece() != null) {piece = move.getPromotionPiece().toString();}

        String mateNotification = null;

        if (checkmate) {mateNotification = "Checkmate";} else if (stalemate) {mateNotification = "Stalemate";}
        notification += username + " moved from " + start + " to " + end;

        if (piece != null) {notification += " and promoted to " + piece;}
        if (!checkmate && check) {notification += " - Check";}

        if (checkmate || stalemate) {
            try {gameService.updateGame(id, "GAME", "OVER", game);} catch (Exception e) {
                message = new ServerMessage(ServerMessageType.ERROR);
                message.setErrorMessage(e.getMessage());
                connections.send(id, session, message);
                return;}
            var checkMate = new ServerMessage(ServerMessageType.NOTIFICATION);
            checkMate.setMessage(mateNotification);
            notify(id, session, checkMate);
            connections.send(id, session, checkMate);}
        broadcast.setMessage(notification);
        message.setGame(game);
        connections.send(id, session, message);
        notify(id, session, message);
        notify(id, session, broadcast);
    }

    private String parsePosition(String position) {
        String col = position.substring(3, 4);
        String row = position.substring(1, 2);

        col = switch (col) {
            case "8" -> "h";
            case "7" -> "g";
            case "6" -> "f";
            case "5" -> "e";
            case "4" -> "d";
            case "3" -> "c";
            case "2" -> "b";
            default -> "a";
        };

        return col + row;
    }

    private boolean authenticated(String auth) throws IOException {
        boolean authenticated;
        try {
            authenticated =  authService.authenticate(auth);
        } catch (DataAccessException e) {
            authenticated = false;
        }
        return authenticated;
    }

    private void enter(String username, String auth, ChessGame.TeamColor team, int id, Session session) throws IOException {
        connections.add(id, session);

        try {
            username = authService.getUsername(auth);
        } catch (Exception e) {
            ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            message.setErrorMessage("Unauthorized");
            connections.send(id, session, message);
            return;
        }
        
        if (!authenticated(auth)) {
            ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            message.setErrorMessage("Unauthorized");
            connections.send(id, session, message);
            return;
        }

        ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        ServerMessage loadMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);

        GameList games;

        try {
            games = gameService.listGames();
        } catch (DataAccessException e) {
            throw new IOException();
        }

        GameData game;

        if (!games.containsKey(id)) {
            loadMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            loadMessage.setErrorMessage("Invalid game ID");
            connections.send(id, session, loadMessage);
            return;
        } else {
            game = games.get(id);
            loadMessage.setGame(game.game());
        }

        if (team == null) {
            message.setMessage(username + " is observing the game");
        } else {
            message.setMessage(username + " joined the game as " + team.toString());
            if (team == ChessGame.TeamColor.BLACK) {
                try {
                    gameService.updateGame(id, game.whiteUsername(), username, game.game());
                } catch (Exception e) {
                    loadMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
                    loadMessage.setErrorMessage(e.getMessage());
                    connections.send(id, session, loadMessage);
                    return;
                }
            } else {
                try {
                    gameService.updateGame(id, username, game.blackUsername(), game.game());
                } catch (Exception e) {
                    loadMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
                    loadMessage.setErrorMessage(e.getMessage());
                    connections.send(id, session, loadMessage);
                    return;
                }
            }
        }
        
        connections.send(id, session, loadMessage);
        notify(id, session, message);
    }

    private void exit(String username, String auth, ChessGame.TeamColor team, int id, Session session) throws IOException {
        connections.add(id, session);
        
        if (!authenticated(auth)) {
            ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            message.setErrorMessage("Unauthorized");
            connections.send(id, session, message);
            return;
        }

        try {
            username = authService.getUsername(auth);
        } catch (Exception e) {
            ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            message.setErrorMessage("Unauthorized");
            connections.send(id, session, message);
            return;
        }

        GameData game;
        try {
            game = gameService.listGames().get(id);

        } catch (DataAccessException e) {
            ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            message.setErrorMessage("Bad game ID");
            connections.send(id, session, message);
            return;
        }

        if (game.blackUsername() != null && game.blackUsername().equals(username)) {
            team = ChessGame.TeamColor.BLACK;
        } else if (game.whiteUsername() != null && game.whiteUsername().equals(username)) {
            team = ChessGame.TeamColor.WHITE;
        } else {
            team = null;
        }

        var message = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);

        if (team == ChessGame.TeamColor.BLACK) {
            game = new GameData(id, game.whiteUsername(), null, game.gameName(), game.game());
        } else if (team == ChessGame.TeamColor.WHITE) {
            game = new GameData(id, null, game.blackUsername(), game.gameName(), game.game());
        } else {
            message.setMessage(username + " is no longer observing");
            notify(id, session, message);
            connections.remove(id, session);
            return;
        }

        try {
            gameService.updateGame(id, game.whiteUsername(), game.blackUsername(), game.game());
        } catch (DataAccessException e) {
            message = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            message.setErrorMessage(e.getMessage());
            connections.send(id, session, message);
            return;
        }
        
        message.setMessage(username + " left the game");

        notify(id, session, message);
        connections.remove(id, session);
    }

    private void resign(String username, String auth, int id, Session session) throws IOException {
        connections.add(id, session);
        
        if (!authenticated(auth)) {
            ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            message.setErrorMessage("Unauthorized");
            connections.send(id, session, message);
            return;
        }

        try {
            username = authService.getUsername(auth);
        } catch (Exception e) {
            ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            message.setErrorMessage("Unauthorized");
            connections.send(id, session, message);
            return;
        }

        GameList games;
        GameData game;

        try {
            games = gameService.listGames();
            game = games.get(id);
            
            String black = game.blackUsername();
            String white = game.whiteUsername();
            if (black == null) {
                black = "";
            }
            if (white == null) {
                white = "";
            }
            
            if (!black.equals(username) && !white.equals(username)) {
                ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
                message.setErrorMessage("Observers cannot resign");
                connections.send(id, session, message);
                return;
            }
        } catch (Exception e) {
            ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            message.setErrorMessage(e.getMessage());
            connections.send(id, session, message);
            return;
        }

        var message = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);

        try {
                gameService.updateGame(id, "GAME", "OVER", game.game());
            } catch (Exception e) {
                message = new ServerMessage(ServerMessageType.ERROR);
                message.setErrorMessage(e.getMessage());
                connections.send(id, session, message);
                return;
            }

        message.setMessage(username + " forfeit the game");

        notify(id, session, message);
        connections.send(id, session, message);
        connections.remove(id, session);
    }
}