package it.polimi.se2019.server.net.socket;

import it.polimi.se2019.server.net.CommandHandler;
import it.polimi.se2019.util.NetMessage;
import it.polimi.se2019.util.Request;
import it.polimi.se2019.util.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * This class contains is used from the server to talk to the clients.
 * Client's sockets are stored in clientSocketMap.
 * To send a message to a client the server needs to know the client's color.
 * This class should be used from the VirtualView (server-side) to contact the View (client-side).
 */

public class SocketServer {
    private static final Logger logger = Logger.getLogger(SocketServer.class.getName());
    private ServerSocket serverSocket;

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        while (!serverSocket.isClosed()) {
            new ClientHandler(serverSocket.accept()).start();
        }
    }

    public static class ClientHandler extends Thread {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private CommandHandler commandHandler;

        private ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                this.commandHandler = new CommandHandler(this);

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    Request request = (Request) new Request(new NetMessage(null), null).deserialize(inputLine);
                    this.commandHandler.handle(request);
                }

                in.close();
                out.close();
                clientSocket.close();
            } catch (IOException e) {
                // do something if connection fails
            }
        }

        public void send(String message) throws IOException {
            out.println(message);
        }
    }
}
