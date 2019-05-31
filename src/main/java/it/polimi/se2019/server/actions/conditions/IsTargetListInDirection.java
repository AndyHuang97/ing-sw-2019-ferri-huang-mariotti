package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.util.ConditionConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IsTargetListInDirection implements Condition {
    @Override
    public boolean check(Game game, Map<String, List<Targetable>> targets) {
        Condition condition = new IsTargetInDirection();
        List<Targetable> targetList = targets.get(ConditionConstants.TARGETLIST);
        List<Targetable> players = new ArrayList<>();
        boolean result = true;

        for(Targetable target : targetList) {
            players.clear();
            players.add(target);
            targets.put(ConditionConstants.TARGET, players);
            if(!condition.check(game, targets)) {
                result = false;
            }
        }

        return result;
    }
}
