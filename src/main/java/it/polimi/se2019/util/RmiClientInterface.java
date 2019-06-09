package it.polimi.se2019.util;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RmiClientInterface extends Remote {
    void send(String message) throws RemoteException;
}
