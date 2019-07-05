package it.polimi.se2019.util;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The RmiServerInterface is the server interface that extends the Remote interface and exposes two methods.
 * The send method is the method used by the client to send messages with its uuid identification and
 * the register method to register a client.
 *
 * @author AH
 *
 */
public interface RmiServerInterface extends Remote {

    /**
     * Send
     *
     * @param message the actual message
     * @param uuid the uuid of the client
     * @throws RemoteException classic rmi exception
     *
     */
    void send(String message, String uuid) throws RemoteException;

    /**
     * Register method
     *
     * @param client the client stub
     * @return the uuid
     * @throws RemoteException classic rmi exception
     *
     */
    String register(RmiClientInterface client) throws RemoteException;
}
