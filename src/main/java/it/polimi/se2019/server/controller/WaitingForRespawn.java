package it.polimi.se2019.server.controller;

import it.polimi.se2019.server.playerActions.PlayerAction;

import java.util.List;

public class WaitingForRespawn implements ControllerState {
    @Override
    public List<PlayerAction> getAllowedPlayerAction(List<PlayerAction> playerActions) {
        return null;
    }

    @Override
    public ControllerState nextState() {
        return null;
    }
}
