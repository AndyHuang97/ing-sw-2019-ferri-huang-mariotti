package it.polimi.se2019.client.net;

import it.polimi.se2019.client.gui.MainApp;
import it.polimi.se2019.client.util.ClientCommandHandler;
import it.polimi.se2019.server.exceptions.PlayerNotFoundException;
import it.polimi.se2019.util.*;

import java.io.*;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Properties;
import java.util.logging.Logger;

public class RmiClient {
    private static final Logger logger = Logger.getLogger(RmiClient.class.getName());
    private String nickname;
    private String serverHost;
    private String uuid;
    private RmiServerInterface server;
    private RmiClientWorker worker;

    public RmiClient(String nickname, String serverHost) {
        this.nickname = nickname;
        this.serverHost = serverHost;
    }

    public void start(MainApp mainApp) {
        try (InputStream input = new FileInputStream("src/main/resources/config.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            int rmiPort = Integer.parseInt(prop.getProperty("rmi.port"));
            this.server = (RmiServerInterface) Naming.lookup("rmi://" + serverHost + ":" + rmiPort + "/adrenalina");
            ClientCommandHandler commandHandler = new ClientCommandHandler(mainApp);
            RmiClientWorker worker = new RmiClientWorker(commandHandler);
            this.uuid = this.server.register(worker);
        } catch (NotBoundException | IOException e) {
            logger.info(e.getMessage());
        }
    }

    public void send(Request request) {
        try {
            this.server.send(request.serialize(), this.uuid);
        } catch (RemoteException e) {
            //TODO: error sending to server
        }
    }

    public class RmiClientWorker extends UnicastRemoteObject implements RmiClientInterface {
        private ClientCommandHandler commandHandler;
        public RmiClientWorker(ClientCommandHandler commandHandler) throws RemoteException {
            this.commandHandler = commandHandler;
        }

        @Override
        public void send(String rawRequest) throws RemoteException {
            Response request = (Response) new Response(null, false, "").deserialize(rawRequest);
            commandHandler.handle(request);
        }


    }
}
