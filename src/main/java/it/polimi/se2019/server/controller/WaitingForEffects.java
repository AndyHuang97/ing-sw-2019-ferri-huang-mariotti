package it.polimi.se2019.server.controller;

import it.polimi.se2019.client.util.Constants;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.playerActions.PlayerAction;
import it.polimi.se2019.server.playerActions.ShootPlayerAction;

import java.util.List;

public class WaitingForEffects implements ControllerState {

    private PlayerAction shootEffect = new ShootPlayerAction(0); // a nameplate variable

    @Override
    public boolean checkActionAvailability(List<PlayerAction> playerActions, Game game, Player player) {

        if (playerActions.size() > 1) {
            return false;
        }
        // assuming there is at least one player action in each message from the client
        if (playerActions.get(0).getId() == Constants.NOP) {
            return true;
        } else if (playerActions.get(0).getId() != Constants.SHOOT) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public ControllerState nextState(List<PlayerAction> playerActions) {
        if (playerActions.get(0).getId() == Constants.NOP) {
            return new WaitingForMainActions();
        } else if (playerActions.get(0).getId() != Constants.SHOOT) {
            return new WaitingForEffects();
        } else {
            return this;
        }
    }
}
