package it.polimi.se2019.server;
import it.polimi.se2019.server.controller.Controller;
import it.polimi.se2019.server.games.GameManager;
import it.polimi.se2019.server.net.rmi.RmiServer;
import it.polimi.se2019.server.net.socket.SocketServer;
import java.util.logging.Logger;
import java.util.Properties;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.File;
import java.io.InputStream;

/**
 * This class is used to setUp the server.
 *
 * @author AH
 *
 */
public class ServerApp {
    private static final Logger logger = Logger.getLogger(ServerApp.class.getName());
    private static SocketServer socketServer;
    private static RmiServer rmiServer;
    public static Controller controller;
    public static GameManager gameManager = new GameManager();
    public static Properties prop = new Properties();

    /**
     * This is the main of the server, it creates the game manager and the controller, it links them plus it starts all
     * the network components
     *
     * @param args the eventual line args
     * @throws IOException if we have problems communicating
     *
     */
    public static void main(String[] args) throws IOException {
        if(args.length > 0) {
            File file = new File(args[0]);
            try (InputStream input = new FileInputStream(file)) {
                prop.load(input);
                logger.info("Finished loading settings from file: " + args[0]);
            } catch (IOException ex) {
                logger.info("Please provide a valid config file");
                System.exit(0);
            }
            // Work with your 'file' object here
        } else {
            logger.info("Please provide a config file");
            System.exit(0);
        }
        System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tF %1$tT %5$s%6$s%n");
        System.setProperty("sun.rmi.transport.tcp.handshakeTimeout", "1000");
        System.setProperty("sun.rmi.transport.tcp.readTimeout", "1000");
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

        gameManager = new GameManager();
        gameManager.init("json/games_dump.json");
        controller = new Controller(gameManager);
        gameManager.setController(controller);
        rmiServer = new RmiServer();
        String rmiHost = prop.getProperty("rmi.host");
        int rmiPort = Integer.parseInt(prop.getProperty("rmi.port"));
        logger.info("Starting RMI Server on port " + rmiPort + " and binded on host " + rmiHost);
        rmiServer.start(rmiHost, rmiPort);
        socketServer = new SocketServer();
        int socketPort = Integer.parseInt(prop.getProperty("socket.port"));
        logger.info("Starting Socket Server on port " + socketPort);
        socketServer.start(socketPort);

    }
}
