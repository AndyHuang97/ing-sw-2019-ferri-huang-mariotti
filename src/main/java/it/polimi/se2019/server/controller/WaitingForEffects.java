package it.polimi.se2019.server.controller;

import it.polimi.se2019.client.util.Constants;
import it.polimi.se2019.server.cards.weapons.Weapon;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.net.CommandHandler;
import it.polimi.se2019.server.playerActions.PlayerAction;
import it.polimi.se2019.server.playerActions.ShootPlayerAction;
import it.polimi.se2019.util.Observer;
import it.polimi.se2019.util.Response;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class WaitingForEffects implements ControllerState {

    private static final int SHOOT_POSITION = 0;

    private Weapon chosenWeapon;
    private ShootPlayerAction shootPlayerAction;
    private ControllerState storedWaitingForMainActions;

    public WaitingForEffects(Weapon chosenWeapon, ControllerState storedWaitingForMainActions) {
        this.chosenWeapon = chosenWeapon;
        this.storedWaitingForMainActions = storedWaitingForMainActions;
    }

    @Override
    public void sendSelectionMessage(CommandHandler commandHandler) {
        try {
            commandHandler.update(new Response(null, true, Constants.SHOOT));
        } catch (Observer.CommunicationError e) {
            Logger.getGlobal().warning(e.toString());
        }
    }

    //TODO check the availability of more optionalEffects as first thing
    @Override
    public ControllerState nextState(List<PlayerAction> playerActions, Game game, Player player) {
        // in this state the controller is expecting to receive a ShootPlayerAction
        if (playerActions.get(SHOOT_POSITION).getId().equals(Constants.SHOOT)) {
            if (playerActions.stream().allMatch(PlayerAction::check)) {
                playerActions.forEach(PlayerAction::run); // there is actually only one action in the list

                shootPlayerAction = (ShootPlayerAction) playerActions.get(SHOOT_POSITION);
                if (game.getCumulativeDamageTargetSet().isEmpty()){ // means no damage was dealt with the run of the action

                    if(!shootPlayerAction.getChosenWeapon().getOptionalEffectList().isEmpty()) {
                        // there are optional effects, no change of player
                        //TODO check the amount of ammo to see directly whether more effects are to be expected?
                        if (game.getCurrentActionUnitsList().size()-1 >= shootPlayerAction.getChosenWeapon().getOptionalEffectList().size()) { // -1 for the basic mode
                            Logger.getGlobal().info("No more optional effects with NO damage");
                            return swapBackToMainAction(game); // no more optional effect
                        } else { // more effects remaining
                            Logger.getGlobal().info("More optional effects left with NO damage");
                            return this;
                        }
                    } else { // no effects to choose from
                        game.getCumulativeDamageTargetSet().clear();
                        return swapBackToMainAction(game);
                    }
                } else { // damage was dealt to someone
                    // Targeting Scope
                    if (player.getCharacterState().getPowerUpBag().stream()
                            .anyMatch(powerUp -> powerUp.getName().split("_")[1].equals(Constants.TARGETING_SCOPE))
                            && player.getCharacterState().getAmmoBag().keySet().stream().anyMatch(color ->player.getCharacterState().getAmmoBag().get(color)>0)) {
                        Logger.getGlobal().info("Attacker has a TargetingScope and at least one ammo");
                        return new WaitingForPowerUps(Constants.TARGETING_SCOPE, this); // power up on current player
                    }
                    // Tagback Grenade
                    game.getCumulativeDamageTargetSet().forEach(t -> Logger.getGlobal().info("DamagedTarget: "+t.getId()));
                    List<Player> powerUpPlayers = game.getCumulativeDamageTargetSet().stream() // checks whether one of the attacked players has a Tagback Grenade
                            .map(t -> (Player) t)
                            .filter(notCurrentPlayer -> !notCurrentPlayer.equals(player)) // gets the targets who can see the attacker
                            .filter(p -> p.getCharacterState().getTile().getVisibleTargets(game).contains(player))
                            .filter(p -> p.getCharacterState().getPowerUpBag().stream()
                                    .anyMatch(powerUp -> powerUp.getName().split("_")[1].equals(Constants.TAGBACK_GRENADE)))
                            .collect(Collectors.toList());
                    Logger.getGlobal().info("powerUpPlayers is not empty: "+ !powerUpPlayers.isEmpty());
                    powerUpPlayers.forEach(player1 -> Logger.getGlobal().info("powerUpPlayer: "+player1.getId() + "\tPowerUp: "+player1.getCharacterState().getPowerUpBag().get(0).getId()));
                    if (!powerUpPlayers.isEmpty()) {
                        // someone has a tagback, initiates the sequence of powerUp selection
                        WaitingForPowerUps newState = new WaitingForPowerUps(Constants.TAGBACK_GRENADE, this); // powerUp on attacked players
                        newState.getPlayerStack().push(player); // storing the player that was executing the turn
                        newState.getPlayerStack().push(powerUpPlayers.get(0));
                        newState.getAlreadyAskedPlayers().add(powerUpPlayers.get(0));
                        game.setCurrentPlayer(powerUpPlayers.get(0)); // giving control to another player
                        return newState;
                    }
                    // no need to go to WaitingForPowerUps state
                    // Logger.getGlobal().info("CurrentActionUnitList size: " + game.getCurrentActionUnitsList().size());
                    // Logger.getGlobal().info("OptionalEffectList size: " + shootPlayerAction.getChosenWeapon().getOptionalEffectList().size());
                    if (game.getCurrentActionUnitsList().size()-1 >= shootPlayerAction.getChosenWeapon().getOptionalEffectList().size()) { // -1 for the basic mode
                        Logger.getGlobal().info("No more optional effects with damage");
                        return swapBackToMainAction(game); // no more optional effect
                    } else { // more effects remaining
                        Logger.getGlobal().info("More optional effects left with damage");
                        game.getCumulativeDamageTargetSet().clear();
                        //game.getCurrentActionUnitsList().forEach(au -> System.out.println(au.getId()));
                        return this;
                    }
                }
            } else {
                Logger.getGlobal().info("ShootPlayerAction check failed");
                return this;
            }
        } else if (playerActions.get(SHOOT_POSITION).getId().equals(Constants.NOP)) { // do not want to perform any shooting
            Logger.getGlobal().info("NOP detected");
            return swapBackToMainAction(game);
        }
        Logger.getGlobal().info("Invalid action");
        return this;
    }

    /**
     * The nextEffectOrAction method is used to detect the cases in which no more optionalEffects are expected
     * when returning from the WaitingForPowerUp state.
     * @return this state if more effects are left, storedWaitingForMainActions otherwise.
     */
    public ControllerState nextEffectOrAction(Game game) {
        if(!shootPlayerAction.getChosenWeapon().getOptionalEffectList().isEmpty()) {
            // there are optional effects, no change of player
            Logger.getGlobal().info("CurrentActionUnitList size: " + game.getCurrentActionUnitsList().size());
            Logger.getGlobal().info("OptionalEffectList size: " + shootPlayerAction.getChosenWeapon().getOptionalEffectList().size());
            //TODO check the amount of ammo to see directly whether more effects are to be expected?
            if (game.getCurrentActionUnitsList().size()-1 >= shootPlayerAction.getChosenWeapon().getOptionalEffectList().size()) { // -1 for the basic mode
                Logger.getGlobal().info("No more optional effects left");
                return swapBackToMainAction(game); // no more optional effect
            } else { // more effects remaining
                Logger.getGlobal().info("More optional effects left");
                return this;
            }
        }
        Logger.getGlobal().info("OptionalEffectList was empty");
        return swapBackToMainAction(game);
    }

    private ControllerState swapBackToMainAction(Game game) {
        if (!game.getCurrentActionUnitsList().isEmpty()) {
            // at least one shot was performed
            chosenWeapon.setLoaded(false);
        }
        // added this to reset the current weapon in use by the current player
        game.setCurrentWeapon(null);
        game.getCumulativeDamageTargetSet().clear();
        return ((WaitingForMainActions) storedWaitingForMainActions).nextPlayerOrReloadRespawn(game, game.getCurrentPlayer()); // should go back to the WaitingForMainActions it came from
    }
}
