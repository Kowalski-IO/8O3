package io.kowalski.ozone.servlet.chat;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import com.hazelcast.core.HazelcastInstance;

@Singleton
@WebServlet(name = "Chat Servlet", urlPatterns = { "/chat" })
public class ChatServlet extends WebSocketServlet {

    private static final long serialVersionUID = -6034903516905575037L;

    private final String serverName;
    private final HazelcastInstance hazelcast;
    private final ConcurrentHashMap<String, Session> sessionsMap;

    @Inject
    public ChatServlet(final @Named("serverName") String serverName, final HazelcastInstance hazelcast,
            final @Named("sessionsMap") ConcurrentHashMap<String, Session> sessionsMap) {
        this.serverName = serverName;
        this.hazelcast = hazelcast;
        this.sessionsMap = sessionsMap;
    }

    @Override
    public void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        response.getWriter().println("HTTP GET method not implemented.");
    }

    @Override
    public void configure(final WebSocketServletFactory factory) {
        factory.getPolicy().setIdleTimeout(100000);
        factory.setCreator(new ChatSocketCreator(serverName, hazelcast, sessionsMap));
    }

}
