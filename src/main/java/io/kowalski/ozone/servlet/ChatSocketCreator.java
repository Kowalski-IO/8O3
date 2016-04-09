package io.kowalski.ozone.servlet;

import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;

import com.hazelcast.core.HazelcastInstance;

public class ChatSocketCreator implements WebSocketCreator {

    private final String serverName;
    private final HazelcastInstance hazelcast;
    private final ConcurrentHashMap<String, Session> sessionsMap;

    public ChatSocketCreator(final String serverName, final HazelcastInstance hazelcast,
            final ConcurrentHashMap<String, Session> sessionsMap) {
        this.serverName = serverName;
        this.hazelcast = hazelcast;
        this.sessionsMap = sessionsMap;
    }

    @Override
    public Object createWebSocket(final ServletUpgradeRequest req, final ServletUpgradeResponse resp) {
        return new ChatSocket(serverName, hazelcast, sessionsMap);
    }
}