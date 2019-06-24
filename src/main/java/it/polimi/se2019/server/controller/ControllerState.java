package it.polimi.se2019.server.controller;

import it.polimi.se2019.server.playerActions.PlayerAction;

import java.util.List;

interface ControllerState {
    /**
     * @param playerActions List<PlayerAction> containing the PlayerActions that needs to be checked
     * @return a List<PlayerAction> containing the actions allowed during the specific turn phase represented
     *         by the Controller State
     */
    List<PlayerAction> getAllowedPlayerActions(List<PlayerAction> playerActions);

    /**
     * This method must be run after running the allowed player actions, and it's meant to be used
     * to set the next ControllerState of the controller for the game of the PlayerActions
     * @return the next ControllerState (turn phase) based on which action have been executed
     */
    ControllerState nextState();
}
