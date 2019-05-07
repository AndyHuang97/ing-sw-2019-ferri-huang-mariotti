package it.polimi.se2019.server.net;
import it.polimi.se2019.util.Request;
import it.polimi.se2019.util.Response;

public class CommandHandler {

    public synchronized Response handle(Request request) {
        String message = request.getMessage();
        String nickname = request.getNickname();
        return null;
    }
}
