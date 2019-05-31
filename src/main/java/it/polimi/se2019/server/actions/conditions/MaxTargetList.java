package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.util.CommandConstants;

import java.util.List;
import java.util.Map;

public class MaxTargetList implements Condition {

    private Integer amount;

    public MaxTargetList(Integer amount) {
        this.amount = amount;
    }

    @Override
    public boolean check(Game game, Map<String, List<Targetable>> targets) {
        return targets.get(CommandConstants.TARGETLIST).size() <= amount;
    }
}
