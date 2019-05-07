package it.polimi.se2019.server.net.rmi;

import it.polimi.se2019.util.NetMsg;
import sun.nio.ch.Net;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RmiInterface extends Remote {

    NetMsg send(NetMsg message) throws RemoteException;
}
