package it.polimi.se2019.server;
import it.polimi.se2019.server.games.GameManager;
import it.polimi.se2019.server.net.rmi.RmiServer;
import it.polimi.se2019.server.net.socket.SocketServer;
import java.util.logging.Logger;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * This class is used to run the server.
 */
public class ServerApp {
    private static final Logger logger = Logger.getLogger(ServerApp.class.getName());
    private static SocketServer socketServer;
    private static RmiServer rmiServer;
    public static GameManager gameManager = new GameManager();

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
        try (InputStream input = new FileInputStream("src/main/resources/config.properties")) {

            Properties prop = new Properties();

            // load a properties file
            prop.load(input);

            gameManager = new GameManager();
            gameManager.init(prop.getProperty("dump.filename"));
            rmiServer = new RmiServer();
            String rmiHost = prop.getProperty("rmi.host");
            int rmiPort = Integer.parseInt(prop.getProperty("rmi.port"));
            logger.info("Starting RMI Server on port " + rmiPort + " and binded on host " + rmiHost);
            rmiServer.start(rmiHost, rmiPort);
            socketServer = new SocketServer();
            int socketPort = Integer.parseInt(prop.getProperty("socket.port"));
            logger.info("Starting Socket Server on port " + socketPort);
            socketServer.start(socketPort);

        } catch (IOException ex) {
            logger.info(ex.toString());
        }
    }
}
