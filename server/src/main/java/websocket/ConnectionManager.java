package websocket;

import org.eclipse.jetty.websocket.api.Session;

import com.google.gson.Gson;

import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, Set<Session>> connections = new ConcurrentHashMap<>();


    public void add(int id, Session session) {
        connections.computeIfAbsent(id, k -> ConcurrentHashMap.newKeySet()).add(session);
    }

    public void remove(int id, Session session) {
        var set = connections.get(id);
        if (set != null) {
            set.remove(session);
        }
    }

    public void broadcast(int id, Session excludeSession, ServerMessage notification) throws IOException {
        String msg = new Gson().toJson(notification);
        var set = connections.get(id);
        if (set == null) {
            return;
        }

        for (Session s : set) {
            if (s.isOpen() && !s.equals(excludeSession)) {
                s.getRemote().sendString(msg);
            }
        }
    }

    public void send(int id, Session session, ServerMessage notification) throws IOException {
        var set = connections.get(id);
        if (set == null) {
            return;
        }

        String msg = new Gson().toJson(notification);

        for (Session s : set) {
            if (s.isOpen() && s.equals(session)) {
                s.getRemote().sendString(msg);
            }
        }
    }
}
