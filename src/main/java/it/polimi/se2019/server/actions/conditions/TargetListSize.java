package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;

import java.util.List;
import java.util.Map;

public class TargetListSize implements Condition {

    private Integer amount;

    public TargetListSize(Integer amount) {
        this.amount = amount;
    }

    @Override
    public boolean check(Game game, Map<String, List<Targetable>> targets) {

        return targets.get("targetList").size() == amount.intValue();
    }
}
