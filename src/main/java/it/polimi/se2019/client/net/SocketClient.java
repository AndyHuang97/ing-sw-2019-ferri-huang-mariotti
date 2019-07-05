package it.polimi.se2019.client.net;


import it.polimi.se2019.client.View;
import it.polimi.se2019.client.cli.ClientCli;
import it.polimi.se2019.client.gui.ClientGui;
import it.polimi.se2019.client.util.ClientCommandHandler;
import it.polimi.se2019.util.NetMessage;
import it.polimi.se2019.util.Request;
import it.polimi.se2019.util.Response;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.logging.Logger;

/**
 * The socket client is similar to the socket server but more simple due to the fact that it only needs to handle a single connection.
 * This implementation is very standard.
 *
 * @author FF
 *
 */
public class SocketClient implements NetworkClient {
    private static final Logger logger = Logger.getLogger(SocketClient.class.getName());
    private String nickname;
    private String serverHost;
    private PrintWriter out;
    private Socket socket;

    /**
     * Just a constructor
     *
     * @param nickname the nickname of the player
     * @param serverHost the host of the server
     *
     */
    public SocketClient(String nickname, String serverHost) {
        this.nickname = nickname;
        this.serverHost = serverHost;
    }

    /**
     * Standard start method, it starts the socket and starts the socket client worker to take care of any incoming messages.
     *
     * @param view the view to connect to
     *
     */
    @Override
    public void start(View view) {
        try {
            int socketPort;
            if (view.isCliTrueGuiFalse()) socketPort = Integer.parseInt(ClientCli.prop.getProperty("socket.port"));
            else socketPort = Integer.parseInt(ClientGui.prop.getProperty("socket.port"));
            socket = new Socket(serverHost, socketPort);
            this.out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            ClientCommandHandler commandHandler = new ClientCommandHandler(view);
            new SocketClientWorker(commandHandler, in, this.out, this.nickname).start();
        } catch (IOException e) {
            logger.info(e.getMessage());
        }
    }

    /**
     * Send method, just prints the request to the socket writer
     *
     * @param request the request to be sent
     *
     */
    @Override
    public void send(Request request) {
        this.out.println(request.serialize());
    }

    /**
     * This is the threaded handler of any incoming data from the server, its like another server but on the client side.
     * Structure is very similar to the server.
     *
     */
    public static class SocketClientWorker extends Thread {
        ClientCommandHandler commandHandler;
        BufferedReader in;
        PrintWriter out;
        String nickname;

        /**
         * Just a constructor
         *
         * @param commandHandler the command handler
         * @param in the read buffer
         * @param out the out writer
         * @param nickname the nickname
         *
         */
        public SocketClientWorker(ClientCommandHandler commandHandler, BufferedReader in, PrintWriter out, String nickname) {
            this.commandHandler = commandHandler;
            this.in = in;
            this.out = out;
            this.nickname = nickname;
        }

        /**
         * Same as the server, just having the task to read a message and giving it to the client command handler
         *
         */
        public void run() {
            try {
                String inputLine;
                while ((inputLine = this.in.readLine()) != null) {
                    Response request = (Response) new Response(null, false, "").deserialize(inputLine);
                    commandHandler.handle(request);
                }
            } catch (IOException | NullPointerException e) {
                // do something if connection fails
            }
        }
    }

    /**
     * Closes the socket, useful on exit
     *
     * @throws IOException in case of net errors
     *
     */
    public void close() throws IOException {
        socket.close();
    }
}
