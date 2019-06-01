package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.util.CommandConstants;

import java.util.List;
import java.util.Map;

public class IsTargetNotInTargetList implements Condition {

    private static final int PLAYERPOSITION = 0;

    @Override
    public boolean check(Game game, Map<String, List<Targetable>> targets) {
        Targetable targetPlayer = targets.get(CommandConstants.TARGET).get(PLAYERPOSITION);
        List<Targetable> cumulativeTargetList = targets.get(CommandConstants.CUMULATIVETARGETLIST);
        return !cumulativeTargetList.contains(targetPlayer);
    }
}
