package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.actions.ActionUnit;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExecutedActionUnits implements Condition {

    private List<String> actionUnits;

    public ExecutedActionUnits(List<String> actionUnits) {
        this.actionUnits = actionUnits;
    }

    @Override
    public boolean check(Game game, Map<String, List<Targetable>> targets) {

        return game.getCurrentActionUnitsList().containsAll(actionUnits);
    }
}
