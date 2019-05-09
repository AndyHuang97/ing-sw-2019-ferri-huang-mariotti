package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.games.player.PlayerColor;

import java.util.List;
import java.util.Map;

public class IsTargetNotInTargetList implements Condition {

    private PlayerColor targetColor;
    private List<PlayerColor> damageTargetList;

    public IsTargetNotInTargetList(PlayerColor targetColor, List<PlayerColor> damageTargetList) {
        this.targetColor = targetColor;
        this.damageTargetList = damageTargetList;
    }

    @Override
    public boolean check(Game game, Map<String, List<Targetable>> targets) {
        Targetable targetPlayer = targets.get("target").get(0);
        List<Targetable> cumulativeTargetList = targets.get("cumulativeTargetList");
        return !cumulativeTargetList.contains(targetPlayer);
    }
}
