package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.player.Player;

import java.util.List;

public class MaxTargetList implements Condition {

    private List<Player> targetList;
    private Integer amount;

    public MaxTargetList(List<Player> targetList, Integer amount) {
        this.targetList = targetList;
        this.amount = amount;
    }

    @Override
    public boolean check() {
        return false;
    }
}
