package it.polimi.se2019.server;
import it.polimi.se2019.server.net.socket.SocketServer;

import java.io.IOException;

/**
 * This class is used to run the server.
 */
public class ServerApp {
    private static SocketServer socketServer;

    public static void main(String[] args) throws IOException {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tF %1$tT %5$s%6$s%n");

        /**
         * Initialize all the server's components:
         *  - Model
         *  - Controller
         *  - Virtual-View
         *  - Connect the Clients views with the players views
         *
         * Restore previous games:
         *  - Read saved games file
         *  - Deserialize saved games
         *  - Wait for players to reconnect (connect the players views with the virtual-views)
         */
        socketServer = new SocketServer();
    }
}
