package it.polimi.se2019.server.playeractions;

import it.polimi.se2019.client.util.Constants;
import it.polimi.se2019.server.cards.Card;
import it.polimi.se2019.server.exceptions.UnpackingException;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.player.Player;

import java.util.List;

public class NoOperation extends PlayerAction {

    public NoOperation(Game game, Player player) {super(game, player);}
    public NoOperation(int amount) {
        super(amount);
    }

    @Override
    public void unpack(List<Targetable> params) throws UnpackingException {

    }

    @Override
    public void run() {

    }

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
