package it.polimi.se2019.server.net.socket;

import it.polimi.se2019.server.games.player.PlayerColor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * This class contains is used from the server to talk to the clients.
 * Client's sockets are stored in clientSocketMap.
 * To send a message to a client the server needs to know the client's color.
 * This class should be used from the VirtualView (server-side) to contact the View (client-side).
 */
public class Server {
    private ServerSocket serverSocket;
    private Map<PlayerColor, Socket> clientSocketMap;

    public Server() throws IOException {
        serverSocket = new ServerSocket(666);
        clientSocketMap = new HashMap<>();
    }

    public void AddClient(PlayerColor color) throws IOException {
        Socket clientSocket = serverSocket.accept();

        clientSocketMap.putIfAbsent(color, clientSocket);
    }

    public Socket RetriveClient(PlayerColor color) {
        return clientSocketMap.get(color);
    }

    public void sendMessage(PlayerColor color) {

    }
}
