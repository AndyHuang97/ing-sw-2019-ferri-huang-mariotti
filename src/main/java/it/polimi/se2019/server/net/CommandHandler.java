package it.polimi.se2019.server.net;
import it.polimi.se2019.server.ServerApp;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.util.Message;
import it.polimi.se2019.util.Observer;
import it.polimi.se2019.util.Request;
import it.polimi.se2019.util.Response;
import java.util.logging.Logger;

/**
 * The CommandHandler act as a virtual view.
 * The virtual view receives messages from the client via network, then it parses the message
 * and notifies the Controller.
 */
public class CommandHandler extends Observable<Request> implements Observer<Response> {
    private static final Logger logger = Logger.getLogger(CommandHandler.class.getName());

    public synchronized Response handle(Request request) {
        // log request
        Message message = request.getMessage();
        String nickname = request.getNickname();
        logger.info(message.toString());
        logger.info(nickname);

        // here it needs to parse the message!
        notify(request);

        // ServerApp.gameManager.dumpToFile();
        // TODO decide how and where to manage the response message
        return new Response(null, false,  "");
    }

    @Override
    public void update(Response response) {
        /**
         * Response on move done
         */
    }

    public void reportError(ErrorResponse errorResponse) {
        /**
         * print error message
         */
    }
}