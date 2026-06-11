package websocket;

import com.google.gson.Gson;

import chess.ChessGame;
import exception.ResponseException;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.*;
import websocket.messages.*;
import server.Server;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();

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
                case CONNECT -> enter(action.getUsername(), action.getTeam(), action.getGameID(), ctx.session);
                case LEAVE -> exit(action.getUsername(), action.getTeam(), action.getGameID(), ctx.session);
                case MAKE_MOVE -> enter(action.getUsername(), action.getTeam(), action.getGameID(), ctx.session);
                case RESIGN -> exit(action.getAuthToken(), action.getTeam(), action.getGameID(), ctx.session);
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

    private void enter(String username, ChessGame.TeamColor team, int id, Session session) throws IOException {
        connections.add(id, session);
        ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);

        if (team == null) {
            message.setMessage(username + " is observing the game");
        } else {
            message.setMessage(username + " joined the game as " + team.toString());
        }
        notify(id, session, message);
    }

    private void exit(String username, ChessGame.TeamColor team, int id, Session session) throws IOException {
        var message = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);

        if (team == null) {
            message.setMessage(username + " is no longer observing");
        } else {
            message.setMessage(username + " left the game");
        }
        
        connections.broadcast(id, session, message);
        connections.remove(id, session);
    }
}