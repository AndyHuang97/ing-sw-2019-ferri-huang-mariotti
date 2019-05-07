package it.polimi.se2019.server.net.rmi;

import it.polimi.se2019.util.NetMsg;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class RmiClient extends UnicastRemoteObject implements RmiInterface {

    private String nickname;

    public static void main(String[] args) {
        try {

            // setup
            NetMsg request = null;
            Scanner s = new Scanner(System.in);
            System.out.println("Enter your nickname and press Enter:");
            String name = s.nextLine().trim();

            // create the instance of the client
            RmiInterface client = new RmiClient(name);

            // get the server remote object
            RmiInterface server = (RmiInterface) Naming.lookup("//localhost:1099/rmi");

            //send a msg to the server that is connected
            String msg="["+((RmiClient)client).getNickname()+"] got connected";
            server.send(request, ((RmiClient) client).getNickname());

            System.out.println("[System] Chat Remote Object is ready:");



            //keep sending msgs
            while(true) {

                msg=s.nextLine().trim();
                msg="["+((RmiClient)client).getNickname()+"] "+msg;
                //set up the server remote object with the remote object of the client
                ((RmiServer)server).setClient(client);
                server.send(request, ((RmiClient) client).getNickname());
            }

        }catch (Exception e) {
            System.out.println("[System] Server failed: " + e);
        }
    }

    public RmiClient(String nickname) throws RemoteException {
        this.nickname = nickname;
    }

    /**
     *
     * @param msg msg is a Response object sent from server to client
     * @param nickname
     * @throws RemoteException
     */
    @Override
    public void send(NetMsg msg, String nickname) throws RemoteException {

    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
