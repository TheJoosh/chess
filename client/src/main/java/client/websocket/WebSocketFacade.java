package client.websocket;

import com.google.gson.Gson;

import chess.ChessGame;
import exception.ResponseException;
import websocket.messages.ServerMessage;
import websocket.commands.UserGameCommand;

import jakarta.websocket.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@ClientEndpoint
public class WebSocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;

    public static final String PURPLE = "\u001B[35m";
    public static final String RESET = "\u001B[0m";

    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            if (!url.endsWith("/ws")) {
                url = url + "/ws";
            }

            URI socketURI = new URI(url);

            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage notification = new Gson().fromJson(message, ServerMessage.class);
                    notificationHandler.notify(notification);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            System.out.print(PURPLE);
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage() + "\n" + RESET);
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void joinGame(String username, String auth, int gameID, ChessGame.TeamColor team) throws ResponseException {
        try {
            var action = new UserGameCommand(UserGameCommand.CommandType.CONNECT, auth, gameID);
            action.setUsername(username);
            action.setTeam(team);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    public void leaveGame(String username, String auth, Integer gameID, ChessGame.TeamColor team) throws ResponseException {
        try {
            var action = new UserGameCommand(UserGameCommand.CommandType.LEAVE, auth, gameID);
            action.setTeam(team);
            action.setUsername(username);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

}