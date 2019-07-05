package it.polimi.se2019.server.rmisocfac;

import java.io.*;
import java.net.*;
import java.rmi.server.*;

public class CustomRMISocketFactory implements RMIClientSocketFactory, Serializable {
    private transient Socket socket;

    public Socket createSocket(String host, int port) throws IOException {
        socket = new Socket();
        socket.setSoTimeout(1000);
        socket.setSoLinger(false, 0);
        socket.connect(new InetSocketAddress(host, port), 1000);
        return socket;
    }

    public ServerSocket createServerSocket(int port) throws IOException {
        return new ServerSocket(port);
    }

    public void closeSocket() throws IOException {
        socket.close();
    }
}
