package it.polimi.se2019.server.controller;

import it.polimi.se2019.client.util.Constants;
import it.polimi.se2019.server.exceptions.IllegalPlayerActionException;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.playerActions.CompositeAction;
import it.polimi.se2019.server.playerActions.MovePlayerAction;
import it.polimi.se2019.server.playerActions.PlayerAction;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This ControllerState represent the turn phase after the movement phase, the player should be able to shoot or grab
 * or reload so this ControllerState will allow those actions.
 */
public class WaitingForMainActions implements ControllerState {
    private ControllerState nextState = null;

    /**
     * @param playerActions List<PlayerAction> containing the PlayerActions (in order) that needs to be checked
     * @param game
     * @param player
     * @return
     */
    @Override
    public boolean checkActionAvailability(List<PlayerAction> playerActions, Game game, Player player) {

        return checkPlayerActionAvailability(playerActions, game, player);

        /*
        // try to parse grab
        ControllerState waitingForGrab = new WaitingForGrab();
        allowedActions = waitingForGrab.checkActionAvailability(playerActions);

        if (!allowedActions.isEmpty()) {
             nextState = waitingForGrab.nextState();
             return allowedActions;
        }

        ControllerState waitingForShoot = new WaitingForShoot();
        allowedActions = waitingForShoot.checkActionAvailability(playerActions);

        if (!allowedActions.isEmpty()) {
            nextState = waitingForShoot.nextState();
            return allowedActions;
        }

        ControllerState waitingForReload = new WaitingForReload();
        allowedActions = waitingForReload.checkActionAvailability(playerActions);

        if (!allowedActions.isEmpty()) {
            nextState = waitingForReload.nextState();
            return allowedActions;
        } else {

        }

        return allowedActions;
         */
    }

    @Override
    public ControllerState nextState(List<PlayerAction> playerActions) {
        return nextState;
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
}
