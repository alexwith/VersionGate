package net.versiongate.standalone;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        final Server server = new Server();
        server.start();
    }
}
