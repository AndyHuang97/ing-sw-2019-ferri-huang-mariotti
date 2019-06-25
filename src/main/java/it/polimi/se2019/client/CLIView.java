package it.polimi.se2019.client;

import java.io.*;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Logger;

public class CLIView extends View {

    private boolean active;
    private boolean connected;
    private PrintStream out;
    private Scanner in;


    public CLIView(PrintStream out) {

        this.out = out;
        this.active = true;
        this.connected = false;
        try(Scanner in = new Scanner(System.in)) {
            this.in = in;
            while (isActive()) {
                askInput();
                String input = in.nextLine();
                CLIView.this.notify(input);
            }
        }

    }

    @Override
    public void askInput() {
        if (!connected) {
            try (InputStream input = new FileInputStream("src/main/resources/config.properties")) {
                Properties prop = new Properties();
                prop.load(input);

                out.println("Choose a nickname:");
                String name = in.nextLine();
                out.println("Choose the server IP(default="+ prop.getProperty("server.host") +"): press enter to skip");
                String ip = in.nextLine();
                if (ip.equals("")) {
                    ip = prop.getProperty("server.host");
                }
                out.println("Choose the connection type {SOCKET,RMI}");
                String connectionType = in.nextLine();
                out.println("Choose a map {0,1,2,3}:");
                String map = in.nextLine();

                super.connect(name, ip, connectionType, map);
            } catch (IOException e) {
                Logger.getGlobal().warning(e.toString());
            }
        }

        /*
        out.println("Choose an option:\n" +
                "1) show board;\n" +
                "2) show players' stats;\n" +
                "3) take an action.");

         */

    }

    @Override
    public void showMessage(String message) {

    }

    @Override
    public void reportError(String error) {

    }

    @Override
    public void showGame() {

    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
