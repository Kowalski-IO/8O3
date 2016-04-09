package io.kowalski.ozone.guice;

import com.google.inject.servlet.ServletModule;

import io.kowalski.ozone.servlet.ChatServlet;

public class OzoneServletModule extends ServletModule {

    @Override
    protected void configureServlets() {
        bind(ChatServlet.class);
        serve("/chat").with(ChatServlet.class);
    }

}
