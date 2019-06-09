package it.polimi.se2019.util;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RmiServerInterface extends Remote {
    void send(String message, String uuid) throws RemoteException;
    String register(RmiClientInterface client) throws RemoteException;
}
