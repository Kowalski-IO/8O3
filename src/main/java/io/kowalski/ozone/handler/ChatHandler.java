package io.kowalski.ozone.handler;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.jetty.websocket.api.Session;

import com.google.gson.Gson;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IQueue;

import io.kowalski.ozone.models.Message;

public class ChatHandler implements Runnable {

    private final String serverName;
    private final HazelcastInstance hazelcast;
    private final ConcurrentHashMap<String, Session> sessionsMap;
    private final Gson gson;

    @Inject
    public ChatHandler(final @Named("serverName") String serverName, final HazelcastInstance hazelcast,
            final @Named("sessionsMap") ConcurrentHashMap<String, Session> sessionsMap) {
        this.serverName = serverName;
        this.hazelcast = hazelcast;
        this.sessionsMap = sessionsMap;
        gson = new Gson();
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            handleMessages();
        }
    }

    private void handleMessages() {
        final IQueue<Message> inboundMessages = hazelcast.getQueue(serverName);
        try {
            final Message message = inboundMessages.take();
            if (message != null) {
                sendMessage(message);
            }
        } catch (final InterruptedException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(final Message message) throws IOException {
        final Session session = sessionsMap.get(message.getRecipient());

        if (session != null && session.isOpen()) {
            session.getRemote().sendString(gson.toJson(message));
        }

    }

}
