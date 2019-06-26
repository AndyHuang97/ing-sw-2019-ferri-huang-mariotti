package it.polimi.se2019.server.controller;

import it.polimi.se2019.client.util.Constants;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.playerActions.PlayerAction;

import java.util.List;

public class WaitingForRespawn implements ControllerState {

    @Override
    public boolean checkActionAvailability(List<PlayerAction> playerActions, Game game, Player player) {
        return false;
    }

    @Override
    public ControllerState nextState(List<PlayerAction> playerActions) {
        if (playerActions.get(0).getId().equals(Constants.POWERUP)) {
            return new WaitingForMainActions();
        } else {
            return this;
        }
    }
}
