package it.polimi.se2019.util;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The RmiServerInterface is the server interface that extends the Remote interface and exposes two methods.
 * The send method is the method used by the client to send messages with its uuid identification and
 * the register method to register a client.
 *
 */
public interface RmiServerInterface extends Remote {
    void send(String message, String uuid) throws RemoteException;
    String register(RmiClientInterface client) throws RemoteException;
}
