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

public class RmiServer {

    private static final Logger logger = Logger.getLogger(SocketServer.class.getName());
    private Map<String, CommandHandler> commandHandlerPicker;

    public RmiServer() {
        this.commandHandlerPicker = new HashMap<>();
    }

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

    public class RmiWorker extends UnicastRemoteObject implements RmiServerInterface {

        public RmiWorker() throws RemoteException {
        }

        @Override
        public void send(String rawRequest, String uuid) throws RemoteException {
            Request request = (Request) new Request(new NetMessage(null), null).deserialize(rawRequest);
            if (commandHandlerPicker.containsKey(uuid)) {
                commandHandlerPicker.get(uuid).handle(request);
            }
        }

        public String register(RmiClientInterface client) throws RemoteException {
            String uuid = UUID.randomUUID().toString();
            commandHandlerPicker.put(uuid, new CommandHandler(client));
            return uuid;
        }
    }
}
