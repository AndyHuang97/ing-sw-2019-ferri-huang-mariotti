package it.polimi.se2019.client.net;

import it.polimi.se2019.client.View;
import it.polimi.se2019.client.util.ClientCommandHandler;
import it.polimi.se2019.util.*;

import java.io.*;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.logging.Logger;

/**
 * The rmi client sets the rmi connection up, creates the client stub to pass to the server and in general handles all
 * the incoming requests from the server to be passed to the client command handler and the request to the server to be outed through here.
 *
 * @author FF
 *
 */
public class RmiClient implements NetworkClient {
    private static final Logger logger = Logger.getLogger(RmiClient.class.getName());
    private String nickname;
    private String serverHost;
    private String uuid;
    private RmiServerInterface server;
    private RmiClientWorker worker;

    /**
     * The constructor
     *
     * @param nickname the user nickname
     * @param serverHost the server host
     *
     */
    public RmiClient(String nickname, String serverHost) {
        this.nickname = nickname;
        this.serverHost = serverHost;
    }

    /**
     * Standard start method, its a bit more powerful because it creates the client command handler and the stub and performs the registration to the server
     *
     * @param view the view to be connected to the command handler
     *
     */
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

    /**
     * Send method, calls the server stub and sends the request. Its prefilled with the uuid.
     *
     * @param request the request to be sent
     *
     */
    @Override
    public void send(Request request) {
        try {
            this.server.send(request.serialize(), this.uuid);
        } catch (RemoteException e) {
            //TODO: error sending to server
        }
    }

    /**
     * This subclass is where the request from the server gets handles, it uses the client command handler to process the
     * received request
     *
     */
    public class RmiClientWorker extends UnicastRemoteObject implements RmiClientInterface {
        private ClientCommandHandler commandHandler;

        /**
         * Default constructor.
         *
         * @param commandHandler the command handler to be used
         * @throws RemoteException in case the rmi fails
         *
         */
        public RmiClientWorker(ClientCommandHandler commandHandler) throws RemoteException {
            this.commandHandler = commandHandler;
        }

        /**
         * When we receive something from the server it gets processed by the command handler
         *
         * @param rawRequest the string request from the server
         * @throws RemoteException in case the rmi fails
         *
         */
        @Override
        public void send(String rawRequest) throws RemoteException {
            Response request = (Response) new Response(null, false, "").deserialize(rawRequest);
            commandHandler.handle(request);
        }


    }
}
