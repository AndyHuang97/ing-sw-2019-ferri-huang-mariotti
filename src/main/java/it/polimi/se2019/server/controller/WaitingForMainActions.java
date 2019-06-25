package it.polimi.se2019.server.controller;

import it.polimi.se2019.server.playerActions.PlayerAction;

import java.util.List;

/**
 * This ControllerState represent the turn phase after the movement phase, the player should be able to shoot or grab
 * or reload so this ControllerState will allow those actions.
 */
public class WaitingForMainActions implements ControllerState {
    private ControllerState nextState = null;

    /**
     * @param playerActions List<PlayerAction> containing the PlayerActions (in order) that needs to be checked
     * @return
     */
    @Override
    public List<PlayerAction> getAllowedPlayerActions(List<PlayerAction> playerActions) {
        List<PlayerAction> allowedActions;

        // try to parse grab
        ControllerState waitingForGrab = new WaitingForGrab();
        allowedActions = waitingForGrab.getAllowedPlayerActions(playerActions);

        if (!allowedActions.isEmpty()) {
             nextState = waitingForGrab.nextState();
             return allowedActions;
        }

        ControllerState waitingForShoot = new WaitingForShoot();
        allowedActions = waitingForShoot.getAllowedPlayerActions(playerActions);

        if (!allowedActions.isEmpty()) {
            nextState = waitingForShoot.nextState();
            return allowedActions;
        }

        ControllerState waitingForReload = new WaitingForReload();
        allowedActions = waitingForReload.getAllowedPlayerActions(playerActions);

        if (!allowedActions.isEmpty()) {
            nextState = waitingForReload.nextState();
            return allowedActions;
        } else {

        }

        return allowedActions;
    }

    @Override
    public ControllerState nextState() {
        return nextState;
    }
}
