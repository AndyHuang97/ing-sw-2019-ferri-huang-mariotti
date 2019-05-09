package it.polimi.se2019.server.users;

import it.polimi.se2019.server.playerActions.PlayerAction;
import it.polimi.se2019.server.actions.Action;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.player.Player;

import java.util.List;

public class ConcreteAction extends PlayerAction {
    public ConcreteAction(Game game, Player player, Action action) {
        super(game, player, action);
    }

    @Override
    public void unpack(List params) {

    }

    @Override
    public void run() {

    }
}
