package it.polimi.se2019.client.net;


import it.polimi.se2019.client.View;
import it.polimi.se2019.client.util.ClientCommandHandler;
import it.polimi.se2019.server.exceptions.ConfigurationReadException;
import it.polimi.se2019.util.NetMessage;
import it.polimi.se2019.util.Request;
import it.polimi.se2019.util.Response;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.logging.Logger;

public class SocketClient implements NetworkClient {
    private static final Logger logger = Logger.getLogger(SocketClient.class.getName());
    private static final int DEFAULTPORT = 4321;

    private String nickname;
    private String serverHost;
    private PrintWriter out;

    public SocketClient(String nickname, String serverHost) {
        this.nickname = nickname;
        this.serverHost = serverHost;
    }

    @Override
    public void start(View view) {
        int socketPort = DEFAULTPORT;
        try (InputStream input = new FileInputStream("src/main/resources/config.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            socketPort = Integer.parseInt(prop.getProperty("socket.port"));
        } catch (IOException e) {
            logger.info(e.getMessage());
            throw new ConfigurationReadException();
        }

        try (Socket socket = new Socket(serverHost, socketPort)) {
            this.out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            ClientCommandHandler commandHandler = new ClientCommandHandler(view);
            new SocketClientWorker(commandHandler, in, this.out, this.nickname).start();
        } catch (IOException e) {
            logger.info(e.getMessage());
        }
    }

    @Override
    public void send(Request request) {
        this.out.println(request.serialize());
    }

    public static class SocketClientWorker extends Thread {
        ClientCommandHandler commandHandler;
        BufferedReader in;
        PrintWriter out;
        String nickname;

        public SocketClientWorker(ClientCommandHandler commandHandler, BufferedReader in, PrintWriter out, String nickname) {
            this.commandHandler = commandHandler;
            this.in = in;
            this.out = out;
            this.nickname = nickname;
        }

        public void run(){
            try {
                String inputLine;
                while ((inputLine = this.in.readLine()) != null) {
                    Response request = (Response) new Response(null, false, "").deserialize(inputLine);
                    // custom code to handle ping pong socket sequence
                    if (request.getMessage().equals("ping")) {
                        Map<String, List<String>> socketPayload = new HashMap<>();
                        socketPayload.put("pong", new ArrayList<>());
                        this.out.println(new Request(new NetMessage(socketPayload), this.nickname).serialize());
                    }
                    commandHandler.handle(request);
                }
            } catch (IOException e) {
                // do something if connection fails
            }
        }
    }
}
