package it.polimi.se2019.server.controller;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.net.CommandHandler;
import it.polimi.se2019.server.playerActions.PlayerAction;

import java.util.List;

interface ControllerState {
    /**
     *
     * @param commandHandler
     * @return sends a message to the correct commandHandler of the current player
     */
    //TODO get the correct commandHandler
    void sendSelectionMessage(CommandHandler commandHandler);

    /**
     * This method contains all the logic of a state. It checks whether the input is among those allowed in the state.
     * If if fails it stays in the same state and keeps waiting for the same input, otherwise it performs the check
     * and run methods of the actions, with possible modifications on the model and then goes to a new state.
     * @param playerActions the list of actions received from the player
     * @param game the game on which to execute the actions
     * @param player the player sending the input
     * @return the new state of the controlelr
     */
    ControllerState nextState(List<PlayerAction> playerActions, Game game, Player player);
}
