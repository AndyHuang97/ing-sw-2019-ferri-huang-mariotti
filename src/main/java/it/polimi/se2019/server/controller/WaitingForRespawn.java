package it.polimi.se2019.server.controller;

import it.polimi.se2019.client.util.Constants;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.games.player.PlayerColor;
import it.polimi.se2019.server.net.CommandHandler;
import it.polimi.se2019.server.playeractions.PlayerAction;
import it.polimi.se2019.util.Observer;
import it.polimi.se2019.util.Response;

import java.util.List;
import java.util.Stack;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class WaitingForRespawn extends ControllerState {

    private static final int POWERUP_POSITION = 0;
    private Stack<Player> playerStack = new Stack<>();

    @Override
    public void sendSelectionMessage(CommandHandler commandHandler) {
        try { // asks the current player for a power up
            Logger.getGlobal().info("Sending respawn selection message...");
            commandHandler.update(new Response(null, true, Constants.RESPAWN));
        } catch (Observer.CommunicationError e) {
            Logger.getGlobal().warning(e.getMessage());
        }
    }


    //TODO call addDeath, swapValueBar, reset damageBar. double kill(da implementare, e aggiornare l'update dello score)
    @Override
    public ControllerState nextState(List<PlayerAction> playerActions, Game game, Player player) {

        if (player.getCharacterState().isFirstSpawn() && !player.getCharacterState().isDead()) {
            if (playerActions.get(POWERUP_POSITION).getId().equals(Constants.RESPAWN)) {
                if (playerActions.get(POWERUP_POSITION).check()) {
                    { // first spawn
                        playerActions.get(POWERUP_POSITION).run();
                        player.getCharacterState().setFirstSpawn(false);
                        Logger.getGlobal().info("First Spawn: " + player.getId());
                        return new WaitingForMainActions(); // go to action selection
                    }
                }
            }
            return this; // did not find a valid power up action, stay in this state; a new selection message will be sent from this state
        } else { // not first spawn
                if (!player.getCharacterState().isDead()) { // this step does NOT require any input it's at the end of a turn
                    playerStack.push(player); // store the player that is ending the turn
                    Logger.getGlobal().info("Pushed player: current Player - " + player.getUserData().getNickname());
                } else {
                    Player p = playerStack.pop(); // pops the dead player that is respawning and get his input
                    Logger.getGlobal().info("Popped player:" + p.getUserData().getNickname());
                    if (playerActions.get(POWERUP_POSITION).getId().equals(Constants.RESPAWN)) {
                        if (playerActions.get(POWERUP_POSITION).check()) {
                                playerActions.get(POWERUP_POSITION).run(); // spawns the player
                        }
                    } else {
                        Logger.getGlobal().info("Expecting a correct input from a dead player.");
                        return this; // tries to get input again for the dead player
                    }
                }

                Logger.getGlobal().info("Any dead player: " + game.getActivePlayerList().stream().anyMatch(p -> p.getCharacterState().isDead()));
                if (game.getActivePlayerList().stream().anyMatch(p -> p.getCharacterState().isDead())) { // if someone is dead
                    List<Player> deadPlayers = game.getActivePlayerList().stream()
                            .filter(p -> p.getCharacterState().isDead()).collect(Collectors.toList());

                    Player deadPlayer = deadPlayers.get(0);
                    System.out.println("dead: " + deadPlayer.getUserData().getNickname());

                    playerStack.push(deadPlayer); // pushes the dead player to respawn

                    boolean isOverkill = deadPlayer.getCharacterState().getDamageBar().size() > 11;

                    Logger.getGlobal().info("Is overkill: " + isOverkill);

                    // give a mark to the player that overkilled deadPlayer
                    if (isOverkill) {
                        // who killed dead player?
                        PlayerColor color = deadPlayer.getCharacterState().getDamageBar().get(11);
                        Player killer = game.getPlayerByColor(color);
                        System.out.println("killer: " + killer.getUserData().getNickname());

                        killer.getCharacterState().addMarker(deadPlayer.getColor(), 1);
                    }

                    game.addDeath(deadPlayer, isOverkill);

                    Logger.getGlobal().info("Pushed player: dead Player " + deadPlayers.get(0).getUserData().getNickname());
                    game.setCurrentPlayer(deadPlayers.get(0)); // give control to one of the dead players, until everyone has spawned
                    Logger.getGlobal().info("Giving control to a dead player");
                    return this; // stays in this state until everyone is spawned
                } else { // nobody is dead
                    player = playerStack.pop();// gives back the player that was ending the turn
                    Logger.getGlobal().info("Popped player:" + player.getId());
                    player.getCharacterState().setFirstSpawn(false);
                    game.setCurrentPlayer(player);// resumes the turn cycle
                    game.updateTurn(); // resumes the turn cycle
                    Logger.getGlobal().info("Next Player: "+game.getCurrentPlayer().getId());
                    if (game.getCurrentPlayer().getCharacterState().isFirstSpawn()) {
                        return new WaitingForRespawn(); // if next player has not spawned yet
                    } else {
                        return new WaitingForMainActions();
                    }
                }
            }
        }
}
