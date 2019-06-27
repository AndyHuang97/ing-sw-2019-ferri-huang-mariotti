package it.polimi.se2019.server.controller;

import it.polimi.se2019.client.util.Constants;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.net.CommandHandler;
import it.polimi.se2019.server.playerActions.PlayerAction;
import it.polimi.se2019.util.Observer;
import it.polimi.se2019.util.Response;

import java.util.List;
import java.util.Stack;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class WaitingForRespawn implements ControllerState {

    private static final int POWERUP_POSITION = 0;
    private Stack<Player> playerStack = new Stack<>();

    @Override
    public void sendSelectionMessage(CommandHandler commandHandler) {
        try { // asks the current player for a power up
            commandHandler.update(new Response(null, true, Constants.RESPAWN));
        } catch (Observer.CommunicationError e) {
            Logger.getGlobal().warning(e.getMessage());
        }
    }

    @Override
    public ControllerState nextState(List<PlayerAction> playerActions, Game game, Player player) {

        if (player.getCharacterState().isFirstSpawn()) {
            if (playerActions.get(POWERUP_POSITION).getId().equals(Constants.POWERUP)) {
                if (playerActions.get(POWERUP_POSITION).check()) {
                    { // first spawn
                        playerActions.get(POWERUP_POSITION).run();
                        player.getCharacterState().setFirstSpawn(false);
                        return new WaitingForMainActions(); // go to action selection
                    }
                }
            }
            return this; // did not find a valid power up action, stay in this state; a new selection message will be sent from this state
        } else { // not first spawn
                if (!player.getCharacterState().isDead()) { // this step does NOT require any input it's at the end of a turn
                    playerStack.push(player); // store the player that is ending the turn

                } else {
                    playerStack.pop(); // pops the dead player that is respawning and get his input
                    if (playerActions.get(POWERUP_POSITION).getId().equals(Constants.POWERUP)) {
                        if (playerActions.get(POWERUP_POSITION).check()) {
                                playerActions.get(POWERUP_POSITION).run(); // spawns the player
                        }
                    }
                    return this; // tries to get input again for the dead player
                }
                if (game.getPlayerList().stream().anyMatch(p -> p.getCharacterState().isDead())) { // if someone is dead
                    List<Player> deadPlayers = game.getPlayerList().stream()
                            .filter(p -> p.getCharacterState().isDead()).collect(Collectors.toList());
                    playerStack.push(deadPlayers.get(0)); // pushes the dead player to respawn
                    game.setCurrentPlayer(deadPlayers.get(0)); // give control to one of the dead players, until everyone has spawned
                    return this; // stays in this state until everyone is spawned
                } else { // nobody is dead
                    player = playerStack.pop();// gives back the player that was ending the turn
                    player.getCharacterState().setFirstSpawn(false);
                    game.setCurrentPlayer(player);
                    game.nextCurrentPlayer(); // resumes the turn cycle
                    return new WaitingForMainActions();
                }
            }
        }
}
