package it.polimi.se2019.client.net;


import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.util.Request;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class SocketClient {
    private static final Logger logger = Logger.getLogger(SocketClient.class.getName());
    private String nickname;
    private String serverHost;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public SocketClient(String nickname, String serverHost) {
        this.nickname = nickname;
        this.serverHost = serverHost;
    }

    public void start() {
        try (InputStream input = new FileInputStream("src/main/resources/config.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            int socketPort = Integer.parseInt(prop.getProperty("socket.port"));
            socket = new Socket(serverHost, socketPort);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            logger.info((Supplier<String>) e);
        }
    }

    public void send(String message) {
        out.println(message);
    }

    public BufferedReader getIn() {
        return in;
    }
}
