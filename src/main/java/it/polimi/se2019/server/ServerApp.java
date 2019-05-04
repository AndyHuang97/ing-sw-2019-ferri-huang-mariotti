package it.polimi.se2019.server;
import it.polimi.se2019.server.net.socket.Server;

import java.io.IOException;
import java.util.logging.Logger;
import static java.util.logging.Level.INFO;

/**
 * This class is used to run the server.
 */
public class ServerApp {
    private static Server socketServer;

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
        socketServer = new Server();
    }
}
