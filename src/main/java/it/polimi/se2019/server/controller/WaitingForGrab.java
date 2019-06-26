package it.polimi.se2019.server.controller;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.playerActions.PlayerAction;

import java.util.List;

@Deprecated
public class WaitingForGrab implements ControllerState {
    @Override
    public boolean checkActionAvailability(List<PlayerAction> playerActions, Game game, Player player) {
        return false;
    }

    @Override
    public ControllerState nextState(List<PlayerAction> playerActions) {
        return null;
    }
}
