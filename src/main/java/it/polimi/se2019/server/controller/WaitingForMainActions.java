package it.polimi.se2019.server.controller;

import it.polimi.se2019.client.util.Constants;
import it.polimi.se2019.server.cards.weapons.Weapon;
import it.polimi.se2019.server.exceptions.IllegalPlayerActionException;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.net.CommandHandler;
import it.polimi.se2019.server.playerActions.CompositeAction;
import it.polimi.se2019.server.playerActions.MovePlayerAction;
import it.polimi.se2019.server.playerActions.PlayerAction;
import it.polimi.se2019.util.Observer;
import it.polimi.se2019.util.Response;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This ControllerState represent the turn phase after the movement phase, the player should be able to shoot or grab
 * or reload so this ControllerState will allow those actions.
 */
public class WaitingForMainActions implements ControllerState {

    private static final int NORMAL_ACTION_NUMBER = 2;
    private static final int FRENZY_BEFORE_NUMBER = 2;
    private static final int FRENZY_AFTER_NUMBER = 1;

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
        if (!checkPlayerActionAvailability(playerActions, game, player)) { // action was not even available
            return this; // stay in the same state and do nothing, only wait for correct input
        }
        if (playerActions.stream().allMatch(PlayerAction::check)) {
            playerActions.forEach(PlayerAction::run);
            int counterLimit = getCounterLimit(game, player);
            actionCounter++;

            PlayerAction shootWeaponSelection = playerActions.stream().filter(playerAction -> playerAction.getId().equals(Constants.SHOOT_WEAPON))
                    .findFirst().orElse(null);
            if (shootWeaponSelection != null) {
                // there is a Shoot action, switch to the shoot sequence in WaitingForEffects state
                Weapon chosenWeapon = (Weapon) shootWeaponSelection.getCard(); // cannot return null because of the if...
                return new WaitingForEffects(chosenWeapon, this);
            }
            // no shoot action or no optional effects
            if (game.isFrenzy()) {
                if (game.getPlayerList().stream().anyMatch(p -> p.getCharacterState().isDead())) {
                    // creates a new WaitingForRespawn state and gets nextState to initiate the respawn sequence
                    WaitingForRespawn newState = new WaitingForRespawn();
                    return newState.nextState(playerActions, game, player);
                } else { // no kills in final frenzy action
                    if (actionCounter == counterLimit) {
                        game.nextCurrentPlayer(); // consumed all actions in frenzy mode, give control to another player
                        return new WaitingForMainActions(); // new player, reset all
                    }
                    return this; // keeps track of the actionCounter
                }
            } else { // not frenzy
                if (actionCounter == counterLimit) { // consumed all actions in normal mode, nextPlayer is delegated to WaitingForReload state
                    return new WaitingForReload(); // in normal mode, respawn is after Reload
                } else { // still an action left
                    // could receive a pass(NOP) message to skip the turn
                    if (playerActions.get(0).getId().equals(Constants.NOP)) {
                        game.nextCurrentPlayer();
                        if (game.getCurrentPlayer().getCharacterState().isFirstSpawn()) {
                            return new WaitingForRespawn(); // first spawn
                        } else {
                            return new WaitingForMainActions(); // new player reset all
                        }
                    }
                    return this; // keeps track of the action actionCounter
                }
            }
        }
        return this; // invalid action because of input selection

    }

    /**
     * The checkPlayerActionAvailability method controls whether the input action that is being processed
     * is an action contained in the permitted action list of the sender player.
     * It throws an IllegalPlayerActionException when the action is not allowed.
     * @param playerActionList the action that was passed as input from the client.
     * @param game is the game related to the sender of the input.
     * @param player is the sender of the action.
     * @throws IllegalPlayerActionException is thrown when an input action is not in the list of possible actions
     */
    public boolean checkPlayerActionAvailability(List<PlayerAction> playerActionList, Game game, Player player)  {
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
                                        System.out.println("possible:" + possiblePlayerAction.getAmount());
                                        System.out.println("distance:" + game.getBoard().getTileTree()
                                                .distance(mpa.getPlayer().getCharacterState().getTile(), mpa.getMoveList().get(0)));
                                        if (possiblePlayerAction.getAmount() <      // the predicate that checks the distance,
                                                game.getBoard().getTileTree()       // if the selected tile gives a greater distance then the action is not allowed
                                                        .distance(mpa.getPlayer().getCharacterState().getTile(), mpa.getMoveList().get(0))) {
                                            return false;
                                        }
                                        return true;
                                    });
                            System.out.println(res);
                            return res; // the result of the the internal anyMatch, it is returned as value of the external anyMatch
                        }
                );
    }

    /**
     * Gets the counter's limit depending on the game mode, and precedence on the first player.
     * @param game the game on which the game mode is evaluated.
     * @param player the player on which the counter limit is evaluated.
     * @return the counter limit of the player.
     */
    private int getCounterLimit(Game game, Player player){
        if (!game.isFrenzy()) {
            return NORMAL_ACTION_NUMBER;
        } else {
            if (player.getCharacterState().isBeforeFrenzyActivator()) {
                return FRENZY_BEFORE_NUMBER;
            } else {
                return FRENZY_AFTER_NUMBER;
            }
        }
    }
}
