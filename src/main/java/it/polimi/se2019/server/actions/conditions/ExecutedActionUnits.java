package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *  The ExecutedActionUnits condition class checks whether an action unit has been performed.
 *
 * @author andreahuang
 */
public class ExecutedActionUnits implements Condition {

    private List<String> actionUnits;

    /**
     * Default constructor. Constructs the action units to be evaluated in the check when called.
     *
     * @param actionUnits the action units that needs to be evaluated.
     */
    public ExecutedActionUnits(List<String> actionUnits) {
        this.actionUnits = actionUnits;
    }

    /**
     * Checks wether all the action units in the list have been executed.
     *
     * @param game the game on which to perform the evaluation.
     * @param targets the targets is the input of the current player. Either tiles or players.
     * @return true if all action units were executed, false otherwise.
     */
    @Override
    public boolean check(Game game, Map<String, List<Targetable>> targets) {
        boolean result = game.getCurrentActionUnitsList().stream()
                .map(au -> au.getName())
                .collect(Collectors.toList()).containsAll(actionUnits);

        Logger.getGlobal().warning("ExecutedActionUnits: "+ result);
        return result;
    }
}
