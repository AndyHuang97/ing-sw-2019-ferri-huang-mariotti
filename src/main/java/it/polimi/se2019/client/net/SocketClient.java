package it.polimi.se2019.client.net;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Properties;

public class SocketClient {

    private String nickname;
    private String serverHost;
    private Socket socket;
    private PrintWriter out;

    public SocketClient(String nickname, String serverHost) {

        this.nickname = nickname;
        this.serverHost = serverHost;
        setUp();
    }

    public void setUp() {
        try (InputStream input = new FileInputStream("src/main/resources/config.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            int socketPort = Integer.parseInt(prop.getProperty("socket.port"));
            socket = new Socket(serverHost, socketPort);
            out = new PrintWriter(socket.getOutputStream(), true);

            send(nickname);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(String message) {
        out.println(message);
    }
}
