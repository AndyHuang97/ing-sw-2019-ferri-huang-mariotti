package it.polimi.se2019.client.net;

import it.polimi.se2019.client.View;
import it.polimi.se2019.client.util.ClientCommandHandler;
import it.polimi.se2019.util.*;

import java.io.*;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Properties;
import java.util.logging.Logger;

public class RmiClient implements NetworkClient {
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

    @Override
    public void start(View view) {
        try (InputStream input = RmiClient.class.getClassLoader().getResource("config.properties").openStream()) {
            Properties prop = new Properties();
            prop.load(input);
            int rmiPort = Integer.parseInt(prop.getProperty("rmi.port"));
            this.server = (RmiServerInterface) Naming.lookup("rmi://" + serverHost + ":" + rmiPort + "/adrenalina");
            ClientCommandHandler commandHandler = new ClientCommandHandler(view);
            RmiClientWorker worker = new RmiClientWorker(commandHandler);
            this.uuid = this.server.register(worker);
        } catch (NotBoundException | IOException e) {
            logger.info(e.getMessage());
        }
    }

    @Override
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
            if (!request.getMessage().equals("ping")) commandHandler.handle(request);
        }


    }
}
