package it.polimi.se2019.server.playeractions;

import it.polimi.se2019.client.util.Constants;
import it.polimi.se2019.server.cards.Card;
import it.polimi.se2019.server.exceptions.UnpackingException;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.player.Player;

import java.util.List;

/**
 * This class represent a no operation (skip). Like all the PlayerAction can be run and checked, his methods
 * should be run by the Controller.
 *
 * @author Andrea Huang
 */
public class NoOperation extends PlayerAction {

    public NoOperation(Game game, Player player) {super(game, player);}
    public NoOperation(int amount) {
        super(amount);
    }

    /**
     * Unlike the other PlayerAction there is nothing to initialize in this action.
     *
     * @param params every list of Targetable, also null
     * @throws UnpackingException never thrown
     */
    @Override
    public void unpack(List<Targetable> params) throws UnpackingException {
        // no operation, does not require additional data
    }

    /**
     * There is nothing to be run for this particular PlayerAction.
     */
    @Override
    public void run() {
        // no operation, nothing to be run
    }

    /**
     * There is nothing to check for this action as it's ever valid.
     *
     * @return true
     */
    @Override
    public boolean check() {
        return true;
    }

    @Override
    public Card getCard() {
        return null;
    }

    @Override
    public String getId() {
        return Constants.NOP;
    }
}
