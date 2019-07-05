package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.util.CommandConstants;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * This condition checks whether the target selected by the attacker are all different.
 *
 * @author andreahuang
 */
public class DifferentTargetsInList implements Condition {


    /**
     * This condition checks whether the target selected by the attacker are all different.
     *
     * @param game the game on which to perform the evaluation.
     * @param targets the targets is the input of the current player. Either tiles or players.
     * @return the result of the evaluation.
     */
    @Override
    public boolean check(Game game, Map<String, List<Targetable>> targets) {
        List<Targetable> targetList = targets.get(CommandConstants.TARGETLIST);

        Logger.getGlobal().info("DifferentTargetsInList: "+targetList.stream().allMatch(new HashSet<>()::add));
        return targetList.stream().allMatch(new HashSet<>()::add);
    }
}
