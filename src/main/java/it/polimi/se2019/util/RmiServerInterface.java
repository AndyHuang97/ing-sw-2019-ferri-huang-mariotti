package it.polimi.se2019.util;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The interface for the RMI Server methods
 *
 * @author AH
 *
 */
public interface RmiServerInterface extends Remote {

    /**
     * Planned a send
     *
     * @param message the actual message
     * @param uuid the uuid of the client
     * @throws RemoteException classic rmi exception
     *
     */
    void send(String message, String uuid) throws RemoteException;

    /**
     * Planned a register method
     *
     * @param client the client stub
     * @return the uuid
     * @throws RemoteException classic rmi exception
     *
     */
    String register(RmiClientInterface client) throws RemoteException;
}
