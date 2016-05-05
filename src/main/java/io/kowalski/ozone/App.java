package io.kowalski.ozone;

public class App {

    public static void main(final String[] args) throws Exception {

        final String host = args[0];
        final int port = Integer.parseInt(args[1]);

        final Ozone ozone = new Ozone(host, port);
        new Thread(ozone, host).start();;
    }

}
