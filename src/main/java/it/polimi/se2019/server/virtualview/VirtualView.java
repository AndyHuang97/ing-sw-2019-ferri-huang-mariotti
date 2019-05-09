package it.polimi.se2019.server.virtualview;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.net.CommandHandler;
import it.polimi.se2019.util.Message;
import it.polimi.se2019.util.Observable;
import it.polimi.se2019.util.Observer;

/**
 * The virtual view receives messages from the client via network, then it parses the message
 * and notifies the Controller.
 */
public class VirtualView extends Observable<Message> implements Observer<Game> {

    private CommandHandler commandHandler;

    @Override
    protected void notify(Message message) {
        super.notify(message);
    }

    @Override
    public void update(Game game) {

    }
}
