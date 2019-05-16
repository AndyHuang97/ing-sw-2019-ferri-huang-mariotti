package it.polimi.se2019.server.net;
import it.polimi.se2019.server.ServerApp;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.util.Message;
import it.polimi.se2019.util.Request;
import it.polimi.se2019.util.Response;

import java.util.logging.Logger;

public class CommandHandler {
    private static final Logger logger = Logger.getLogger(CommandHandler.class.getName());

    public synchronized Response handle(Request request) {
        Message message = request.getMessage();
        String nickname = request.getNickname();
        logger.info(message.toString());
        logger.info(nickname);
        // here it needs to parse the message!
        ServerApp.gameManager.dumpToFile();
        return new Response(new Game(), true, "ciao");
    }


}
