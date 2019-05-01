package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.player.PlayerColor;

import java.util.List;

public class IsTargetNotInDamageTargetList implements Condition {

    private PlayerColor targetColor;
    private List<PlayerColor> damageTargetList;

    public IsTargetNotInDamageTargetList(PlayerColor targetColor, List<PlayerColor> damageTargetList) {
        this.targetColor = targetColor;
        this.damageTargetList = damageTargetList;
    }

    @Override
    public boolean check() {
        return false;
    }
}
