package it.polimi.se2019.server.controller;

import it.polimi.se2019.client.util.Constants;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.net.CommandHandler;
import it.polimi.se2019.server.playerActions.PlayerAction;
import it.polimi.se2019.util.Observer;
import it.polimi.se2019.util.Response;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class WaitingForPowerUps implements ControllerState {

    private static final int POWERUP_POSITION = 0;

    private String expectedPowerUp;
    private ControllerState storedWaitingForEffects;
    private Stack<Player> playerStack = new Stack<>();
    private Set<Player> alreadyAskedPlayers = new HashSet<>();

    public WaitingForPowerUps(String expectedPowerUp, ControllerState storedWaitingForEffects) {
        this.expectedPowerUp = expectedPowerUp;
        this.storedWaitingForEffects = storedWaitingForEffects;
    }

    @Override
    public void sendSelectionMessage(CommandHandler commandHandler) {
        try {
            commandHandler.update(new Response(null, true, expectedPowerUp));
        } catch (Observer.CommunicationError e) {
            Logger.getGlobal().warning(e.toString());
        }
    }

    @Override
    public ControllerState nextState(List<PlayerAction> playerActions, Game game, Player player) {
        // could receive a pass(NOP) message to skip the selection of powerUp
        if (playerActions.get(0).getId().equals(Constants.NOP)) {
            Logger.getGlobal().info("Detected a NOP");
            return storedWaitingForEffects;
        }

        if (expectedPowerUp.equals(Constants.TARGETING_SCOPE)) {
            if (playerActions.stream().allMatch(playerAction -> playerAction.getId().equals(expectedPowerUp))) {
                if (playerActions.stream().allMatch(PlayerAction::check)) {
                    playerActions.forEach(PlayerAction::run);
                    return storedWaitingForEffects;
                }
            } else if (playerActions.get(POWERUP_POSITION).getId().equals(Constants.NOP)) {
                return storedWaitingForEffects; // chose to not use powerUp
            }
            return this; // no valid input for Targeting Scope
        }

        if (expectedPowerUp.equals(Constants.TAGBACK_GRENADE)) {
            if (playerActions.stream().allMatch(playerAction -> playerAction.getId().equals(expectedPowerUp)) ||
                    playerActions.get(POWERUP_POSITION).getId().equals(Constants.NOP)) {
                // this block of code will be executed either with a powerUp or with a NOP, in the latter case nothing
                // is performed on the model, but it is still needed to ask the next player for input
                if (playerActions.stream().allMatch(PlayerAction::check)) {
                    playerActions.forEach(PlayerAction::run);

                    playerStack.pop(); // pops the player that sent the correct powerUp to be consumed
                    List<Player> powerUpPlayers = game.getCumulativeDamageTargetSet().stream() // checks whether one of the attacked players has a Tagback Grenade
                            .map(t -> (Player) t).filter(p ->
                                    p.getCharacterState().getPowerUpBag().stream()
                                            .anyMatch(powerUp -> powerUp.getName().split("_")[1].equals(Constants.TAGBACK_GRENADE)))
                            .collect(Collectors.toList());
                    if (!powerUpPlayers.isEmpty()) {
                        // if any player still has a tagback
                        if (!alreadyAskedPlayers.containsAll(powerUpPlayers)) {
                            // alreadyAskedPlayers keeps track of players that were already asked to use the powerUp
                            // if this set contains all the remaining powerUpPlayers with a tagback grenade, it means it
                            // has already asked every player.
                            playerStack.push(powerUpPlayers.get(0));
                            alreadyAskedPlayers.add(powerUpPlayers.get(0));
                            game.setCurrentPlayer(powerUpPlayers.get(0));
                            return this;
                        }
                    }
                    player = playerStack.pop(); // should be the player that was performing the turn
                    game.setCurrentPlayer(player);
                    alreadyAskedPlayers.clear();
                    return storedWaitingForEffects;
                }
            }
        }
        return this; // invalid input
    }

    public Stack<Player> getPlayerStack() {
        return playerStack;
    }

    public Set<Player> getAlreadyAskedPlayers() {
        return alreadyAskedPlayers;
    }
}
