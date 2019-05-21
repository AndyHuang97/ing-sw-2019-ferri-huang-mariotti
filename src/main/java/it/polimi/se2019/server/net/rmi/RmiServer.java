package it.polimi.se2019.server.net.rmi;

import it.polimi.se2019.server.ServerApp;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.net.CommandHandler;
import it.polimi.se2019.server.net.socket.SocketServer;
import it.polimi.se2019.util.NetMsg;
import it.polimi.se2019.util.Request;
import it.polimi.se2019.util.Response;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Logger;

public class RmiServer {

    private static final Logger logger = Logger.getLogger(SocketServer.class.getName());

    public void start(String host, int port) {
        try {
            // bind the object
            System.setProperty("java.rmi.server.hostname", host);
            Naming.rebind(String.format("rmi://localhost:%d/rmi", port), new RmiServerWorker());
            logger.info("RMI server was bound CORRECTLY");
        }catch (Exception e) {
            // catch it
            logger.info("RMI server was bound INCORRECTLY");
        }
    }

    public class RmiServerWorker extends UnicastRemoteObject implements RmiInterface {

        public RmiServerWorker() throws RemoteException {
        }

        @Override
        public NetMsg send(NetMsg request) throws RemoteException {
            return  new Response(new Game(), true, "Hello from rmi.");
            //return new CommandHandler().handle((Request) request);
        }
    }
}
