package io.kowalski.ozone;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.util.EnumSet;

import javax.servlet.DispatcherType;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceFilter;

import io.kowalski.ozone.guice.OzoneModule;
import io.kowalski.ozone.guice.OzoneServletModule;
import io.kowalski.ozone.handler.ChatHandler;

public class App implements Runnable {

    private final String serverName;
    private final int port;

    public App(final String serverName, final int port) {
        this.serverName = serverName;
        this.port = port;
    }

    public static void main(final String[] args) throws Exception {
        final String serverName = args[0];
        int port;
        try {
            port = Integer.parseInt(args[1]);
        } catch (final NumberFormatException e) {
            port = 0;
        }
        new App(serverName, port);
    }

    @Override
    public void run() {
        try {
            final Injector injector = Guice.createInjector(new OzoneServletModule(), new OzoneModule(serverName));

            final ChatHandler chatHandler = injector.getInstance(ChatHandler.class);
            final Thread chatHandlerThread = new Thread(chatHandler, serverName.concat(" ChatHandler"));

            final Server server = new Server(port);

            final Path webrootPath = new File("src/main/resources/assets").toPath().toRealPath();
            final URI webrootUri = webrootPath.toUri();
            final Resource webroot = Resource.newResource(webrootUri);

            final ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);

            context.addFilter(GuiceFilter.class, "/*", EnumSet.allOf(DispatcherType.class));
            context.setContextPath("/");
            context.setBaseResource(webroot);
            context.setWelcomeFiles(new String[] { "index.html" });

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
