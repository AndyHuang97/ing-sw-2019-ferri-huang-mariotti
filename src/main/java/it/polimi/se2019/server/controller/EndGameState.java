package it.polimi.se2019.server.controller;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.net.CommandHandler;
import it.polimi.se2019.server.playeractions.PlayerAction;

import java.util.List;

/**
 * This is the state the controller is at when a game ends
 *
 * @author FF
 *
 */
public class EndGameState extends ControllerState {

    /**
     * No messages are sent to the client
     *
     * @param commandHandler the player commandhandler
     *
     */
    @Override
    public void sendSelectionMessage(CommandHandler commandHandler) {

    }

    /**
     * No next state is available
     *
     * @param playerActions the list of actions received from the player
     * @param game the game on which to execute the actions
     * @param player the player sending the input
     * @return the new state of the controller
     *
     */
    @Override
    public ControllerState nextState(List<PlayerAction> playerActions, Game game, Player player) {

        return null;
    }

}
