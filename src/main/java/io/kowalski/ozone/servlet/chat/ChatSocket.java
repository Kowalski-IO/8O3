package io.kowalski.ozone.servlet.chat;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.common.WebSocketSession;

import com.google.gson.Gson;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IQueue;

import io.kowalski.ozone.models.Constants;
import io.kowalski.ozone.models.Message;

@WebSocket
public class ChatSocket {

    private final String serverName;
    private final HazelcastInstance hazelcast;
    private final ConcurrentHashMap<String, Session> sessionsMap;
    private final Gson gson;

    public ChatSocket(final String serverName, final HazelcastInstance hazelcast, final ConcurrentHashMap<String, Session> sessionsMap) {
        this.serverName = serverName;
        this.hazelcast = hazelcast;
        this.sessionsMap = sessionsMap;
        gson = new Gson();
    }

    @OnWebSocketClose
    public void onClose(final Session session, final int statusCode, final String reason) {
        System.out.println("Close: " + reason);
    }

    @OnWebSocketError
    public void onError(final Throwable t) {
        System.out.println("Error: " + t.getMessage());
    }

    @OnWebSocketConnect
    public void onConnect(final Session session) {
        final Map<String, String> queryParams = parseQueryParams(session);
        final String token = queryParams.get("token");

        final Map<String, String> serverLocationMap = hazelcast.getMap(Constants.USER_SERVER_LOCATION_MAP);
        serverLocationMap.put(token, serverName);
        sessionsMap.put(token, session);

        try {
            session.getRemote().sendString("Welcome to server ".concat(serverName));
        } catch (final IOException e) {
            System.out.println("IO Exception");
        }
    }

    @OnWebSocketMessage
    public void onMessage(final Session session, final String rawMessage) throws IOException {
        final Message message = gson.fromJson(rawMessage, Message.class);

        final Map<String, String> serverLocationMap = hazelcast.getMap(Constants.USER_SERVER_LOCATION_MAP);
        final String handlingServer = serverLocationMap.get(message.getRecipient());

        final IQueue<Message> handlingServerQueue = hazelcast.getQueue(handlingServer);

        handlingServerQueue.add(message);

    }

    private WebSocketSession parseSession(final Session session) {
        return WebSocketSession.class.cast(session);
    }

    private Map<String, String> parseQueryParams(final Session session) {
        final Map<String, String> params = new HashMap<String, String>();

        final List<NameValuePair> rawParams = URLEncodedUtils.parse(parseSession(session).getRequestURI(), "UTF-8");

        for (final NameValuePair rawParam : rawParams) {
            params.put(rawParam.getName(), rawParam.getValue());
        }

        return params;
    }
}
