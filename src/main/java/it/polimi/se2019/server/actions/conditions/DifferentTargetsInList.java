package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.player.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class DifferentTargetsInList implements Condition {

    private List<Player> targetList;

    public DifferentTargetsInList(List<Player> targetList) {
        this.targetList = targetList;
    }

    @Override
    public boolean check(Game game, Map<String, List<Targetable>> targets) {

        return targetList.stream().allMatch(new HashSet<>()::add);
    }
}
