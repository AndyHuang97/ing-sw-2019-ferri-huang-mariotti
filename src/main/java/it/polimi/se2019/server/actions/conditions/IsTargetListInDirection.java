package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.player.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IsTargetListInDirection implements Condition {
    @Override
    public boolean check(Game game, Map<String, List<Targetable>> targets) {
        Condition condition = new IsTargetInDirection();
        List<Targetable> targetList = targets.get("targetList");
        List<Targetable> players = new ArrayList<>();
        boolean result = true;

        for(Targetable target : targetList) {
            players.add(target);
            targets.put("target", players);
            if(!condition.check(game, targets)) {
                result = false;
            }
        }

        return result;
    }
}
