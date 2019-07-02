package it.polimi.se2019.server.controller;

import it.polimi.se2019.client.util.Constants;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.net.CommandHandler;
import it.polimi.se2019.server.playeractions.PlayerAction;
import it.polimi.se2019.server.playeractions.PowerUpAction;
import it.polimi.se2019.util.Observer;
import it.polimi.se2019.util.Response;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class WaitingForPowerUps extends ControllerState {

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

        if (expectedPowerUp.equals(Constants.TARGETING_SCOPE)) {
            // could receive a pass(NOP) message to skip the selection of powerUp
            if (playerActions.get(POWERUP_POSITION).getId().equals(Constants.NOP)) {
                Logger.getGlobal().info("Detected a NOP for Targeting Scope");
                return ((WaitingForEffects)storedWaitingForEffects).nextEffectOrAction(game);
            }
            if (playerActions.stream().allMatch(playerAction ->
                    playerAction.getId().equals(Constants.POWERUP)
                                                    &&
                            ((PowerUpAction) playerAction).getPowerUpsToDiscard().stream().allMatch(powerUp -> powerUp.getName().split("_")[1].equals(Constants.TARGETING_SCOPE)))) {
                if (playerActions.stream().allMatch(ControllerState::checkPlayerActionAndSaveError)) {
                    playerActions.forEach(PlayerAction::run);
                    Logger.getGlobal().info("Targeting Scope going back to WaitingForEffects");
                    return ((WaitingForEffects)storedWaitingForEffects).nextEffectOrAction(game);
                }
            }
            Logger.getGlobal().info("Invalid input for Targeting Scope");
            return this; // no valid input for Targeting Scope
        }

        if (expectedPowerUp.equals(Constants.TAGBACK_GRENADE)) {
            Logger.getGlobal().info("inside Tagback");
            if (playerActions.stream().allMatch(playerAction ->
                    playerAction.getId().equals(Constants.POWERUP)
                                                    &&
                            ((PowerUpAction) playerAction).getPowerUpsToDiscard().stream().allMatch(powerUp -> powerUp.getName().split("_")[1].equals(Constants.TAGBACK_GRENADE)))) {
                Logger.getGlobal().info("Found a Tagback Grenade");
                // this block of code will be executed either with a powerUp or with a NOP, in the latter case nothing
                // is performed on the model, but it is still needed to ask the next player for input
                Logger.getGlobal().info("PlayerActionCheck: " + playerActions.stream().allMatch(ControllerState::checkPlayerActionAndSaveError));
                if (playerActions.stream().allMatch(ControllerState::checkPlayerActionAndSaveError)) {
                    playerActions.forEach(PlayerAction::run); // needed to discard the powerup

                    Player poppedPlayer = playerStack.pop(); // pops the player that sent the correct powerUp to be consumed
                    Logger.getGlobal().info("Popped: "+poppedPlayer.getId());
                    playerStack.forEach(p -> Logger.getGlobal().info("Player at the top of the stack: "+playerStack.peek().getId()));
                    // giving markers to the attacker
                    playerActions.forEach(playerAction ->((PowerUpAction) playerAction).getPowerUpsToDiscard().forEach(powerUp -> playerStack.peek().getCharacterState().addMarker(poppedPlayer.getColor(), 1)));

                    List<Player> powerUpPlayers = game.getCumulativeDamageTargetSet().stream() // checks whether one of the attacked players has a Tagback Grenade
                            .map(t -> (Player) t)
                                    .filter(notCurrentPlayer -> !alreadyAskedPlayers.contains(notCurrentPlayer))
                                    .filter(notCurrentPlayer -> !notCurrentPlayer.equals(playerStack.peek())) // gets the targets who can see the attacker
                                    .filter(p -> p.getCharacterState().getTile().getVisibleTargets(game).contains(playerStack.peek()))
                                    .filter(p -> p.getCharacterState().getPowerUpBag().stream() // check if they have tagback grenade
                                            .anyMatch(powerUp -> powerUp.getName().split("_")[1].equals(Constants.TAGBACK_GRENADE)))
                            .collect(Collectors.toList());
                    Logger.getGlobal().info("powerUpPlayers is not empty: "+ !powerUpPlayers.isEmpty());
                    powerUpPlayers.forEach(player1 -> Logger.getGlobal().info("powerUpPlayer: "+player1.getId() + "\tPowerUp: "+player1.getCharacterState().getPowerUpBag().get(0).getId()));
                    if (!powerUpPlayers.isEmpty()) {
                        // if any player still needs to be asked and has a tagback grenade
                        alreadyAskedPlayers.forEach(player1 -> Logger.getGlobal().info("AlreadyAskedPlayer: "+player1.getId()));
                        playerStack.push(powerUpPlayers.get(0));
                        alreadyAskedPlayers.add(powerUpPlayers.get(0));
                        game.setCurrentPlayer(powerUpPlayers.get(0));
                        Logger.getGlobal().info("More visible people with Tagback Grenade");
                        return this;
                    }
                    Logger.getGlobal().info("CurrentActionUnitList size: " + game.getCurrentActionUnitsList().size());
                    game.getCurrentActionUnitsList().forEach(au -> Logger.getGlobal().info(au.getId()));

                    Player originalPlayer = playerStack.pop(); // should be the player that was performing the turn
                    Logger.getGlobal().info("Popped current player");
                    game.setCurrentPlayer(originalPlayer);
                    alreadyAskedPlayers.clear();
                    Logger.getGlobal().info("Tagback grenade going back to WaitingForEffects");
                    return ((WaitingForEffects)storedWaitingForEffects).nextEffectOrAction(game);
                }
            }
            Logger.getGlobal().info("Invalid input for Tagback Grenade, at least one was not a Tagback Grenade");
            return this;
        }
        Logger.getGlobal().info("Invalid input, keep waiting");
        return this; // invalid input
    }

    public Stack<Player> getPlayerStack() {
        return playerStack;
    }

    public Set<Player> getAlreadyAskedPlayers() {
        return alreadyAskedPlayers;
    }
}
