package it.polimi.se2019.server.controller;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.net.CommandHandler;
import it.polimi.se2019.server.playeractions.PlayerAction;

import java.util.List;

/**
 * This state represents the end of the game. When the controller reaches this state and tries to get the following
 * state using the nextState() a response is sent to each View telling that the game is over.
 *
 * @author Rodolfo Mariotti
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
        // the game is finished, no need for selection messages
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
        //Response response = new Response(null, true, Constants.FINISHGAME);

        // walk-around to send a broadcast message to all the Views
        //game.update(response);

        return null;
    }
}
