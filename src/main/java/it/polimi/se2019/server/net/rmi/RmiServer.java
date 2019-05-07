package it.polimi.se2019.server.net.rmi;

import it.polimi.se2019.util.NetMsg;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RmiServer extends UnicastRemoteObject implements RmiInterface {

    private RmiInterface client;

    public static void main(String[] args) {

        try {

            // create the object to be exposed
            RmiInterface server = new RmiServer();
            // bind the object
            Naming.rebind("//localhost:1099/rmi", server);
            System.out.println("[System] Remote Server Object is ready");

            // keep waiting new messages from shell
            while(true) {
                // read a message (and wait)
                NetMsg response = null;

                // if there is no server skip
                if(((RmiServer)server).getClient() != null) {

                    // when a client calls the remote instance of the server it registers the client
                    RmiInterface client = ((RmiServer)server).getClient();
                    // setup the message
                    client.send(response, "");
                }
            }

        }catch(Exception e) {
            System.out.println("[System] Server failed: " + e);
        }
    }

    public RmiServer() throws RemoteException {
        this.client = null;
    }

    public RmiServer(RmiInterface client) throws RemoteException {
        this.client = client;
    }


    /**
     *
     * @param msg is Request object sent from client to server
     * @param nickname
     * @throws RemoteException
     */
    @Override
    public void send(NetMsg msg, String nickname) throws RemoteException {

    }

    public RmiInterface getClient() {
        return client;
    }

    public void setClient(RmiInterface client) {
        this.client = client;
    }
}
