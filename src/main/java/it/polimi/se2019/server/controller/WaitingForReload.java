package it.polimi.se2019.server.controller;

import it.polimi.se2019.client.util.Constants;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.net.CommandHandler;
import it.polimi.se2019.server.playeractions.PlayerAction;
import it.polimi.se2019.util.Observer;
import it.polimi.se2019.util.Response;

import java.util.List;
import java.util.logging.Logger;

/**
 * This ControllerState represent when we are waiting for the player to gives us a reload command
 *
 *  @author AH
 *
 */
public class WaitingForReload extends ControllerState {

    private static final int RELOAD_POSITION = 0;

    /**
     * Send the message to the client
     *
     * @param commandHandler the client command handler
     *
     */
    @Override
    public void sendSelectionMessage(CommandHandler commandHandler) {
        try {
            commandHandler.update(new Response(null, true, Constants.RELOAD));
        } catch (Observer.CommunicationError e) {
            Logger.getGlobal().warning(e.toString());
        }
    }

    /**
     * After a reload is issued or not we go to the next player (or we respawn the dead guys)
     *
     * @param playerActions the list of actions received from the player
     * @param game the game on which to execute the actions
     * @param player the player sending the input
     * @return the new state of the controller
     *
     */
    @Override
    public ControllerState nextState(List<PlayerAction> playerActions, Game game, Player player) {

        if (playerActions.get(RELOAD_POSITION).getId().equals(Constants.RELOAD) ||
                playerActions.get(RELOAD_POSITION).getId().equals(Constants.NOP)) {
            if (playerActions.stream().allMatch(ControllerState::checkPlayerActionAndSaveError)) {
                playerActions.stream().forEach(PlayerAction::run);

                if (game.getActivePlayerList().stream().anyMatch(p -> p.getCharacterState().isDead())) {
                    WaitingForRespawn newState = new WaitingForRespawn();
                    Logger.getGlobal().info("Someone was killed");
                    return newState.nextState(playerActions, game, player);
                } else {
                    game.updateTurn();
                    if (game.getCurrentPlayer().getCharacterState().isFirstSpawn()) {
                        Logger.getGlobal().info("No one was killed, first spawn");
                        return new WaitingForRespawn();
                    }
                    else {
                        Logger.getGlobal().info("No one was killed, not first spawn");
                        return new WaitingForMainActions();
                    }
                }
            }
        }
        Logger.getGlobal().info("Invalid input");
        return this; // invalid input
    }
}
