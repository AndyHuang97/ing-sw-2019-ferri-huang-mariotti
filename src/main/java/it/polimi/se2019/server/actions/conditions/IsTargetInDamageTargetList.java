package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;

import java.util.List;
import java.util.Map;

public class IsTargetInDamageTargetList implements Condition {

    @Override
    public boolean check(Game game, Map<String, List<Targetable>> targets) {
        Targetable targetPlayer = targets.get("target").get(0);
        List<Targetable> damageTargetList = targets.get("damageTargetList");
        return damageTargetList.contains(targetPlayer);
    }
}
