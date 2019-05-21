package it.polimi.se2019.server.net;
import it.polimi.se2019.server.ServerApp;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.net.socket.SocketServer;
import it.polimi.se2019.util.*;
import java.util.logging.Logger;

/**
 * The CommandHandler act as a virtual view.
 * The virtual view receives messages from the client via network, then it parses the message
 * and notifies the Controller.
 */
public class CommandHandler extends Observable<Request> implements Observer<Response> {

    private static final Logger logger = Logger.getLogger(CommandHandler.class.getName());

    private SocketServer.ClientHandler clientHandler;

    // just for tests
    public CommandHandler() {
    }

    public CommandHandler(SocketServer.ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }


    public synchronized void handle(Request request) {
        // log request
        Message message = request.getMessage();
        String nickname = request.getNickname();
        logger.info(message.toString());
        logger.info(nickname);

        request.setCommandHandler(this);

        // here it needs to parse the message!
        notify(request);

        // TODO update shouldn't be called from here, but from the model with its notify, and it should receive a Response
        update(new Response(new Game(), true, request.getNickname()));

        ServerApp.gameManager.dumpToFile();

    }

    @Override
    public void update(Response response) {
        /**
         * Response on move done
         */
        showMessage(response.serialize());

    }

    public void showMessage(String message) {
        clientHandler.asyncSend(message);
    }

    public void reportError(ErrorResponse errorResponse) {
        /**
         * print error message
         */
    }
}