package it.polimi.se2019.util;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The RmiClientInterface interface is the client interface that extends the Remote interface for RMI communitation.
 * It exposes a send message to be called by the server.
 *
 * @author AH
 *
 */
public interface RmiClientInterface extends Remote {

    /**
     * Send
     *
     * @param message the actual message
     * @throws RemoteException classic rmi exception
     *
     */
    void send(String message) throws RemoteException;
}
