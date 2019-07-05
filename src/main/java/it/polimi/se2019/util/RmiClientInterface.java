package it.polimi.se2019.util;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The RmiClientInterface interface is the client interface that extends the Remote interface for RMI communitation.
 * It exposes a send message to be called by the server.
 *
 */
public interface RmiClientInterface extends Remote {
    void send(String message) throws RemoteException;
}
