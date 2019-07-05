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
 * The socket server handles a connection made using sockets. It handles the send and receive parts. The server task is to accept a connection
 * and start a new thread with a clienthandler inside to manage it. This way we have a single network entry/exit through the client handler and the command handler.
 *
 * @author FF
 *
 */
public class SocketServer {
    private static final Logger logger = Logger.getLogger(SocketServer.class.getName());
    private ServerSocket serverSocket;

    /**
     * Standard start method, it starts the accept connections loop. For each new connections it receive it spawns a new thread
     * to manage it.
     *
     * @param port the socket port to be used
     * @throws IOException in case of bad net errors
     *
     */
    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        while (!serverSocket.isClosed()) {
            new ClientHandler(serverSocket.accept()).start();
        }
    }

    /**
     * This subclass is where a connection from the client gets handled, It provides as a unique point of contact with the client.
     * This is a very standard approach to socket management, nothing fancy.
     *
     */
    public static class ClientHandler extends Thread {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private CommandHandler commandHandler;

        /**
         * Default constructor.
         *
         */
        private ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        /**
         * When a new connection is managed we create the in and out channels from the socket assigned to the client, we than
         * spawn a new command handler to link to this and ew start the while cycle to grab any incoming messages.
         * messages are processed by the command handler.
         *
         */
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

        /**
         * To send something to a specific client, message must be already serialized.
         *
         * @param message the message to send
         * @throws IOException in case of bad network
         *
         */
        public void send(String message) throws IOException {
            out.println(message);
        }
    }
}
