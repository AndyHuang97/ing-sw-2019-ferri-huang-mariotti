package it.polimi.se2019.server.net.socket;

import it.polimi.se2019.server.ServerApp;
import it.polimi.se2019.server.net.CommandHandler;
import it.polimi.se2019.util.Request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
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

    public void stop() throws IOException {
        serverSocket.close();
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
                commandHandler = new CommandHandler(this);

                //String nickname = in.readLine();
                //System.out.println(nickname);

                //out.println("Copy and paste this input for test: {\"message\":{},\"nickname\":\"Jon Snow\"}");
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    Request request = (Request) new Request(null, null).deserialize(inputLine);
                    //out.println(new CommandHandler().handle(request).serialize());

                    // handle(request)
                    commandHandler.handle(request);
                }

                in.close();
                out.close();
                clientSocket.close();
            } catch (IOException e) {
                // do something if connection fails
            }
        }

        public void asyncSend(String message) {
            out.println(message);
        }
    }
}
