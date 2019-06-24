package it.polimi.se2019.server.controller;

import it.polimi.se2019.server.playerActions.PlayerAction;

import java.util.ArrayList;
import java.util.List;

public class WaitingForMainActions implements ControllerState {
    @Override
    public List<PlayerAction> getAllowedPlayerActions(List<PlayerAction> playerActions) {
        List<PlayerAction> allowedPlayerActions = new ArrayList<>();

        for (PlayerAction playerAction : playerActions) {
            if (playerAction.isAvailable(TurnPhase.WAITING_FOR_MAIN_ACTIONS)) {
                allowedPlayerActions.add(playerAction);
            }
        }

        return allowedPlayerActions;
    }

    @Override
    public ControllerState nextState() {
        return null;
    }
}
