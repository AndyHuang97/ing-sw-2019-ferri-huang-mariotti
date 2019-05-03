package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.player.Player;

import java.util.HashSet;
import java.util.List;

public class DifferentTargetsInList implements Condition {

    private List<Player> targetList;

    public DifferentTargetsInList(List<Player> targetList) {
        this.targetList = targetList;
    }

    @Override
    public boolean check() {
        return !targetList.stream().allMatch(new HashSet<>()::add);
    }
}
