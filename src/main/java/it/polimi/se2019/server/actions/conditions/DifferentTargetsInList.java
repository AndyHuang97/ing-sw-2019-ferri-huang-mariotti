package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.util.ConditionConstants;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * This condition checks whether the target selected by the attacker are all different.
 */
public class DifferentTargetsInList implements Condition {

    @Override
    public boolean check(Game game, Map<String, List<Targetable>> targets) {
        List<Targetable> targetList = targets.get(ConditionConstants.TARGETLIST);

        return targetList.stream().allMatch(new HashSet<>()::add);
    }
}
