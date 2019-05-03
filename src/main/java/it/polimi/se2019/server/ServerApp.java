package it.polimi.se2019.server;
import it.polimi.se2019.server.net.socket.Server;

import java.io.IOException;
import java.util.logging.Logger;
import static java.util.logging.Level.INFO;

public class ServerApp {
    private static Server socketServer;

    public static void main(String[] args) throws IOException {

        System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tF %1$tT %5$s%6$s%n");

        socketServer = new Server();

        // mServer.startWelcomeServer();

        // mServer.start();

        // mLogger.log(INFO, "ServerApp Started");

    }
}
