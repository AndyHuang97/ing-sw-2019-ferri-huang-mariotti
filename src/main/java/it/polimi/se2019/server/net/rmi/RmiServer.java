package it.polimi.se2019.server.net.rmi;

import it.polimi.se2019.server.net.CommandHandler;
import it.polimi.se2019.server.net.socket.SocketServer;
import it.polimi.se2019.util.*;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * The rmi server handles a connection made using RMI. It handles just the server part. The server task is to accept a connection
 * and start a new command handler to manage it. This way we have a single network entry/exit through the command handler.
 *
 * @author FF
 *
 */
public class RmiServer {

    private static final Logger logger = Logger.getLogger(SocketServer.class.getName());
    private Map<String, CommandHandler> commandHandlerPicker;

    /**
     * Default constructor just initializes the commandhander map, this is used to assign the request to the correct command
     * handler when we receive one. To do that we assign a uuid to each connection and its up to the client send the uuid along
     * with the message
     *
     */
    public RmiServer() {
        this.commandHandlerPicker = new HashMap<>();
    }

    /**
     * Standard start method, to be noted the property set to allow client to connect even in local network and not just on
     * localhost. Except for that is a usual rmi connection.
     *
     * @param host the server host address to be communicated to the clients to communicate back
     * @param port the rmi port to be used
     *
     */
    public void start(String host, int port) {
        try {
            // bind the object
            System.setProperty("java.rmi.server.hostname", host);
            LocateRegistry.createRegistry(port);
            Naming.rebind("rmi://localhost:" + port + "/adrenalina", new RmiWorker());
        } catch (Exception e) {
            // catch it
            logger.info(e.getMessage());
        }
    }

    /**
     * This subclass is where a connection from the client gets handled, as we said earlier th clients first registers itself
     * and gets assigned a uuid to be sent along each request. During registration serverside its also stored the commandhandler
     * just created and the client stub to communicate back. After registration is complete a client can message the server.
     *
     */
    public class RmiWorker extends UnicastRemoteObject implements RmiServerInterface {

        /**
         * Default constructor
         *
         * @throws RemoteException standard rmi exception in case of problems
         *
         */
        public RmiWorker() throws RemoteException {
        }

        /**
         * The default server receive method, it handles the request using the command handler specific to the user
         *
         * @param rawRequest the serialized string passed by the rmi
         * @param uuid the uuid need to uniquely identify a connection
         * @throws RemoteException standard rmi exception in case of problems
         *
         */
        @Override
        public void send(String rawRequest, String uuid) throws RemoteException {
            Request request = (Request) new Request(new NetMessage(null), null).deserialize(rawRequest);
            if (commandHandlerPicker.containsKey(uuid)) {
                commandHandlerPicker.get(uuid).handle(request);
            }
        }

        /**
         * This is used by the client the first time they connect to register to the server, provide their stub to communicate
         * back to them and to get a uuid
         *
         * @param client the client rmi stub
         * @return the uuid
         * @throws RemoteException standard rmi exception in case of problems
         *
         */
        public String register(RmiClientInterface client) throws RemoteException {
            String uuid = UUID.randomUUID().toString();
            commandHandlerPicker.put(uuid, new CommandHandler(client));
            return uuid;
        }
    }
}
