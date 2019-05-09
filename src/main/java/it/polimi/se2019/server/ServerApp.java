package it.polimi.se2019.server;
import it.polimi.se2019.server.games.GameManager;
import it.polimi.se2019.server.net.rmi.RmiServer;
import it.polimi.se2019.server.net.socket.SocketServer;
import java.util.logging.Logger;
import java.io.IOException;

/**
 * This class is used to run the server.
 */
public class ServerApp {
    private static final Logger logger = Logger.getLogger(ServerApp.class.getName());
    private static SocketServer socketServer;
    private static RmiServer rmiServer;
    public static final GameManager gameManager = new GameManager();

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
        gameManager.init("dump.json");
        rmiServer = new RmiServer();
        String rmiHost = "10.170.4.100";
        int rmiPort = 1111;
        logger.info("Starting RMI Server on port " + rmiPort + " and binded on host " + rmiHost);
        rmiServer.start(rmiHost, rmiPort);
        socketServer = new SocketServer();
        int socketPort = 2222;
        logger.info("Starting Socket Server on port " + socketPort);
        socketServer.start(socketPort);
    }
}
