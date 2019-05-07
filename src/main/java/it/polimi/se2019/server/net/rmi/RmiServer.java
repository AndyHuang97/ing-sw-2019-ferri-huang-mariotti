package it.polimi.se2019.server.net.rmi;

import it.polimi.se2019.server.net.CommandHandler;
import it.polimi.se2019.util.NetMsg;
import it.polimi.se2019.util.Request;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RmiServer {

    public void start(String host, int port) {
        try {
            // bind the object
            System.setProperty("java.rmi.server.hostname", host);
            Naming.rebind(String.format("rmi://localhost:%d/rmi", port), new RmiServerWorker());
        }catch (Exception e) {
            // catch it
        }
    }

    public class RmiServerWorker extends UnicastRemoteObject implements RmiInterface {

        public RmiServerWorker() throws RemoteException {
        }

        @Override
        public NetMsg send(NetMsg request) throws RemoteException {
            return new CommandHandler().handle((Request) request);
        }
    }
}
