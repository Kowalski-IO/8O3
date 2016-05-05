package io.kowalski.ozone;

import java.util.EnumSet;

import javax.servlet.DispatcherType;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceFilter;

import io.kowalski.ozone.guice.OzoneModule;
import io.kowalski.ozone.guice.OzoneServletModule;
import io.kowalski.ozone.handler.ChatHandler;

public class Ozone implements Runnable {

    private final String serverName;
    private final int port;

    public Ozone(final String serverName, final int port) {
        this.serverName = serverName;
        this.port = port;
    }

    public static void main(final String[] args) throws Exception {
        new Ozone("ozone", 5678).run();
    }

    @Override
    public void run() {
        try {
            final Injector injector = Guice.createInjector(new OzoneServletModule(), new OzoneModule(serverName));

            final ChatHandler chatHandler = injector.getInstance(ChatHandler.class);
            final Thread chatHandlerThread = new Thread(chatHandler, serverName.concat(" ChatHandler"));

            final Server server = new Server();

            final HttpConfiguration https = new HttpConfiguration();
            https.addCustomizer(new SecureRequestCustomizer());
            final SslContextFactory sslContextFactory = new SslContextFactory();
            sslContextFactory.setKeyStorePath(Ozone.class.getResource("/keystore.jks").toExternalForm());
            sslContextFactory.setKeyStorePassword("123456");
            sslContextFactory.setKeyManagerPassword("123456");
            final ServerConnector sslConnector = new ServerConnector(server,
                    new SslConnectionFactory(sslContextFactory, "http/1.1"), new HttpConnectionFactory(https));
            sslConnector.setPort(port);

            final String webroot = Ozone.class.getClass().getResource("/assets").toExternalForm();
            System.out.println(webroot);

            final ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);

            context.addFilter(GuiceFilter.class, "/*", EnumSet.allOf(DispatcherType.class));
            context.setContextPath("/");
            context.setResourceBase(webroot);
            context.setWelcomeFiles(new String[] { "index.html" });

            server.setConnectors(new Connector[] { sslConnector });
            server.setHandler(context);

            final ServletHolder holder = new ServletHolder("default", DefaultServlet.class);
            holder.setInitParameter("dirAllowed", "true");

            context.addServlet(holder, "/");

            chatHandlerThread.start();

            server.start();
            server.join();
            chatHandlerThread.interrupt();
        } catch (final Exception e) {
            System.err.println(e);
        }
    }

}
