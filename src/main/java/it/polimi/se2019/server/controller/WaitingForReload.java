package it.polimi.se2019.server.controller;

import it.polimi.se2019.client.util.Constants;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.net.CommandHandler;
import it.polimi.se2019.server.playerActions.PlayerAction;
import it.polimi.se2019.util.Observer;
import it.polimi.se2019.util.Response;

import java.util.List;
import java.util.logging.Logger;

public class WaitingForReload implements ControllerState {

    private static final int RELOAD_POSITION = 0;

    @Override
    public void sendSelectionMessage(CommandHandler commandHandler) {
        try {
            commandHandler.update(new Response(null, true, Constants.RELOAD));
        } catch (Observer.CommunicationError e) {
            Logger.getGlobal().warning(e.toString());
        }
    }

    @Override
    public ControllerState nextState(List<PlayerAction> playerActions, Game game, Player player) {

        if (playerActions.get(RELOAD_POSITION).getId().equals(Constants.RELOAD) ||
                playerActions.get(RELOAD_POSITION).getId().equals(Constants.NOP)) {
            if (playerActions.stream().allMatch(PlayerAction::check)) {
                playerActions.stream().forEach(PlayerAction::run);

                if (game.getPlayerList().stream().anyMatch(p -> p.getCharacterState().isDead())) {
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
                        Logger.getGlobal().info("No one was killedm not first spawn");
                        return new WaitingForMainActions();
                    }
                }
            }
        }
        Logger.getGlobal().info("Invalid input");
        return this; // invalid input
    }
}
