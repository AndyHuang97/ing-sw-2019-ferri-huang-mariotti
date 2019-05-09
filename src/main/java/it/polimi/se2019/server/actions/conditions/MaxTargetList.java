package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.player.Player;

import java.util.List;
import java.util.Map;

public class MaxTargetList implements Condition {

    private List<Player> targetList;
    private Integer amount;

    public MaxTargetList(List<Player> targetList, Integer amount) {
        this.targetList = targetList;
        this.amount = amount;
    }

    @Override
    public boolean check(Game game, Map<String, List<Targetable>> targets) {
        return targets.get("targetList").size() < amount;
    }
}
