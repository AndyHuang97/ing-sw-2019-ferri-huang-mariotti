package it.polimi.se2019.server.controller;

import it.polimi.se2019.client.util.Constants;
import it.polimi.se2019.server.cards.powerup.PowerUp;
import it.polimi.se2019.server.cards.weapons.Weapon;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.net.CommandHandler;
import it.polimi.se2019.server.playeractions.CompositeAction;
import it.polimi.se2019.server.playeractions.MovePlayerAction;
import it.polimi.se2019.server.playeractions.PlayerAction;
import it.polimi.se2019.server.playeractions.PowerUpAction;
import it.polimi.se2019.util.Observer;
import it.polimi.se2019.util.Response;

import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This ControllerState represent the turn phase after the movement phase, the player should be able to shoot or grab
 * or reload so this ControllerState will allow those actions.
 */
public class WaitingForMainActions extends ControllerState {
    private static final String MOVE_ACTION_ERROR_MESSAGE = "Move action failed!";

    private static final int NORMAL_ACTION_NUMBER = 2;
    private static final int BEFORE_FRENZY_NUMBER = 2;
    private static final int AFTER_FRENZY_NUMBER = 1;
    private static final int POWERUP_POSITION = 0;

    /**
     *
     * @param commandHandler @return
     */
    @Override
    public void sendSelectionMessage(CommandHandler commandHandler) {
        try {
            commandHandler.update(new Response(null, true, Constants.MAIN_ACTION));
        } catch (Observer.CommunicationError error) {
            error.printStackTrace();
        }
    }

    private int actionCounter = 0;

    @Override
    public ControllerState nextState(List<PlayerAction> playerActions, Game game, Player player) throws ClassCastException {
        //TODO need to add all the error reports: commandHandler.reportError(playerAction.getErrorMessage());
        if (playerActions.get(POWERUP_POSITION).getId().equals(Constants.POWERUP)) {
            PowerUp powerUp = ((PowerUpAction)playerActions.get(POWERUP_POSITION)).getPowerUpsToDiscard().get(POWERUP_POSITION);
            if (powerUp.getName().split("_")[1].equals(Constants.TELEPORTER) || powerUp.getName().split("_")[1].equals(Constants.NEWTON)) {
                Logger.getGlobal().info("Detected a " + powerUp.getId());
                if (playerActions.stream().allMatch(ControllerState::checkPlayerActionAndSaveError)) {
                    playerActions.forEach(PlayerAction::run);
                }
            }
            return this;
        }

        if (!checkPlayerActionAvailability(playerActions, game, player)) { // action was not even available
            Logger.getGlobal().info("Action not available");
            return this; // stay in the same state and do nothing, only wait for correct input
        }
        // could receive a pass(NOP) message to skip the turn
        if (playerActions.get(0).getId().equals(Constants.NOP)) {
            Logger.getGlobal().info("Detected a NOP");
            if (game.getActivePlayerList().stream().anyMatch(p -> p.getCharacterState().isDead())) {
                // creates a new WaitingForRespawn state and gets nextState to initiate the respawn sequence
                WaitingForRespawn newState = new WaitingForRespawn();
                Logger.getGlobal().info("Someone was killed before NOP");
                return newState.nextState(playerActions, game, player);
            }

            game.updateTurn();

            if (game.getCurrentPlayer().getCharacterState().isFirstSpawn()) {
                Logger.getGlobal().info("Next player has never spawned until now");
                return new WaitingForRespawn(); // first spawn
            } else {
                Logger.getGlobal().info("Next player has already spawned");
                return new WaitingForMainActions(); // new player reset all
            }
        }
        if (playerActions.stream().allMatch(ControllerState::checkPlayerActionAndSaveError)) {
            playerActions.forEach(PlayerAction::run);
            updateCounter();
            Logger.getGlobal().info("action counter: "+actionCounter + "\tcounter limit: "+getCounterLimit(game, player) + "\tplayer "+player.getUserData().getNickname());

            PlayerAction shootWeaponSelection = playerActions.stream().filter(playerAction -> playerAction.getId().equals(Constants.SHOOT_WEAPON))
                    .findFirst().orElse(null);
            if (shootWeaponSelection != null) {
                // there is a Shoot action, switch to the shoot sequence in WaitingForEffects state
                Weapon chosenWeapon = (Weapon) shootWeaponSelection.getCard(); // cannot return null because of the if...
                Logger.getGlobal().info("Detected ShootWeaponSelection");
                game.getCurrentActionUnitsList().clear();
                // adding this to store the weapon currently in use by the player currently playing
                game.setCurrentWeapon(chosenWeapon);
                return new WaitingForEffects(chosenWeapon, this);
            }
            // no shoot weapon selection
            return nextPlayerOrReloadRespawn(game, player);
        }
        Logger.getGlobal().info("Action was available, but check failed");
        return this; // invalid action because of input selection
    }

    public ControllerState nextPlayerOrReloadRespawn(Game game, Player player) {
        if (game.isFrenzy()) {
            if (actionCounter >= getCounterLimit(game, player)) {
                if (game.getActivePlayerList().stream().anyMatch(p -> p.getCharacterState().isDead())) {
                    // creates a new WaitingForRespawn state and gets nextState to initiate the respawn sequence
                    WaitingForRespawn newState = new WaitingForRespawn();
                    Logger.getGlobal().info("Someone was killed in frenzy. No more actions");
                    return newState.nextState(null, game, player); // no playerActions will be evaluated
                } else {// no kills in final frenzy action
                    Supplier<Stream<Player>> beforeFrenzyActivatorPlayers = () -> game.getActivePlayerList().stream().filter(p -> p.getCharacterState().isBeforeFrenzyActivator());
                    if (game.getCurrentPlayer().equals(beforeFrenzyActivatorPlayers.get().collect(Collectors.toList()).get((int) beforeFrenzyActivatorPlayers.get().count()-1))) {
                        if (!game.isFrenzyActivatorEntered()) {
                            game.setFrenzyActivatorEntered(true);
                        } else {
                            Logger.getGlobal().info("Terminating the game");

                            Response response = new Response(null, true, Constants.FINISHGAME);

                            // walk-around to send a broadcast message to all the Views
                            game.update(response);

                            return new EndGameState();
                        }
                    }
                    game.updateTurn(); // consumed all actions in frenzy mode, give control to another player
                    Logger.getGlobal().info("No one was killed in frenzy. No more actions, next player");
                    return new WaitingForMainActions(); // new player, reset all
                }
            } else {
                Logger.getGlobal().info("More actions left in frenzy, get the next action");
                return this; // keeps track of the actionCounter for the current player
            }
        } else { // not frenzy
            if (actionCounter >= getCounterLimit(game, player)) { // consumed all actions in normal mode, nextPlayer is delegated to WaitingForReload state
                Logger.getGlobal().info("Not frenzy. No more actions, go to reload");
                return new WaitingForReload(); // in normal mode, respawn is after Reload
            } else { // still an action left
                Logger.getGlobal().info("Not frenzy. More actions left, get the next action");
                return this; // keeps track of the action actionCounter
            }
        }
        /*
        if (actionCounter >= getCounterLimit(game, player)) { // no actions left
            game.getCurrentActionUnitsList().clear();
            if (game.getActivePlayerList().stream().anyMatch(p -> p.getCharacterState().isDead())) {
                if (game.isFrenzy()) {
                    WaitingForRespawn newState = new WaitingForRespawn();
                    Logger.getGlobal().info("Someone was killed in frenzy. No more actions");
                    return newState.nextState(null, game, player); // do not need for the current player
                } else {
                    Logger.getGlobal().info("Someone was killed in normal. No more actions");
                    return new WaitingForReload();
                }
            }
            else {// no one died
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
        } else {
            Logger.getGlobal().info("Still have some actions left");
            return this;
        }

         */
    }

    /**
     * The checkPlayerActionAvailability method controls whether the input action that is being processed
     * is an action contained in the permitted action list of the sender player.
     *
     * @param playerActionList the action that was passed as input from the client.
     * @param game is the game related to the sender of the input.
     * @param player is the sender of the action.
     */
    private boolean checkPlayerActionAvailability(List<PlayerAction> playerActionList, Game game, Player player)  {
        List<CompositeAction> possibleActions = player.getCharacterState().getPossibleActions(game.isFrenzy());
        return possibleActions.stream()
                // checks whether the different lists of Ids of the possible actions contain the list of Ids of the input
                .filter(composite ->
                        composite.getAction().stream()
                                .map(Targetable::getId)
                                .collect(Collectors.toList()) // list of Ids of a particular possible action
                                .containsAll
                                        (playerActionList.stream() // list of Ids of the input
                                                .map(Targetable::getId)
                                                .collect(Collectors.toList())))
                .anyMatch(composite -> {              // checks whether among the possible actions that have the same Ids as the input, the move action has an allowed distance
                            Supplier<Stream<PlayerAction>> supplier = () ->
                                    composite.getAction().stream()
                                            .filter(possiblePlayerAction -> possiblePlayerAction.getId().equals(Constants.MOVE)); // gets only the Move action
                            if (supplier.get().count()==0) { // checks the presence of possible actions,
                                return true;                 // if there is none it returns true,
                            }                                // otherwise anyMatch would return false with no elements in the stream
                            boolean res = supplier.get()
                                    .anyMatch(possiblePlayerAction -> { // the actual anyMatch that performs the check of the Move's distance
                                        MovePlayerAction mpa = playerActionList.stream()
                                                .filter(playerAction -> playerAction.getId().equals(Constants.MOVE))
                                                .map(pa -> (MovePlayerAction) pa)
                                                .findFirst().orElseThrow(IllegalStateException::new);
                                        int distance = game.getBoard().getTileTree()
                                                .distance(mpa.getPlayer().getCharacterState().getTile(), mpa.getMoveList().get(0));
                                        Logger.getGlobal().info("allowed distance :" + possiblePlayerAction.getAmount());
                                        Logger.getGlobal().info("actual distance:" + distance);
                                        if (possiblePlayerAction.getAmount() < distance || distance == -1) {
                                            // the predicate that checks the distance, if the selected tile gives a
                                            // greater distance then the action is not allowed
                                            // also checks reachability
                                            return false;
                                        }
                                        return true;
                                    });
                            Logger.getGlobal().info(String.valueOf(res));

                            if (!res) addErrorMessage(MOVE_ACTION_ERROR_MESSAGE);

                            return res; // the result of the the internal anyMatch, it is returned as value of the external anyMatch
                        }
                );
    }

    /**
     * Gets the counter's limit depending on the game mode, and precedence on the first player.
     *
     * @param game the game on which the game mode is evaluated.
     * @param player the player on which the counter limit is evaluated.
     * @return the counter limit of the player.
     */
    private int getCounterLimit(Game game, Player player){
        if (!game.isFrenzy()) {
            return NORMAL_ACTION_NUMBER;
        } else {
            Logger.getGlobal().info("is before frenzy activator: " + player.getCharacterState().isBeforeFrenzyActivator());
            if (player.getCharacterState().isBeforeFrenzyActivator()) {
                return BEFORE_FRENZY_NUMBER;
            } else {
                return AFTER_FRENZY_NUMBER;
            }
        }
    }

    //public for testing
    public void updateCounter() {
        actionCounter++;
    }
}
