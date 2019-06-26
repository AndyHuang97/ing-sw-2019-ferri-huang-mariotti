package it.polimi.se2019.server.controller;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.playerActions.PlayerAction;

import java.util.List;

interface ControllerState {
    /**
     * @param playerActions List<PlayerAction> containing the PlayerActions (in order) that needs to be checked
     * @param game
     * @param player
     * @return a List<PlayerAction> containing the actions allowed during the specific turn phase represented
     *         by the Controller State
     */
    boolean checkActionAvailability(List<PlayerAction> playerActions, Game game, Player player);

    /**
     * @ This method must be run after running the allowed player actions, and it's meant to be used
     * to set the next ControllerState of the controller for the game of the PlayerActions
     * @return the next ControllerState (turn phase) based on which action have been executed
     * @param playerActions
     */
    ControllerState nextState(List<PlayerAction> playerActions);
}
