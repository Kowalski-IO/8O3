package io.kowalski.ozone.guice;

import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jetty.websocket.api.Session;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import io.kowalski.ozone.handler.ChatHandler;

public class OzoneModule extends AbstractModule {

    private final String serverName;
    private final HazelcastInstance hazelcast;
    private final ConcurrentHashMap<String, Session> sessionsMap;

    public OzoneModule(final String serverName) {
        this.serverName = serverName;
        hazelcast = Hazelcast.newHazelcastInstance();
        sessionsMap = new ConcurrentHashMap<String, Session>();
    }

    @Override
    protected void configure() {
        bind(ChatHandler.class);
        bind(String.class).annotatedWith(Names.named("serverName")).toInstance(serverName);
        bind(new TypeLiteral<ConcurrentHashMap<String, Session>>(){}).annotatedWith(Names.named("sessionsMap")).toInstance(sessionsMap);
        bind(HazelcastInstance.class).toInstance(hazelcast);
    }

}
