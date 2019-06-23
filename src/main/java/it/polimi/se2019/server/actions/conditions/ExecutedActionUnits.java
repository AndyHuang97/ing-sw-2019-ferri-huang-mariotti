package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ExecutedActionUnits implements Condition {

    private List<String> actionUnits;

    public ExecutedActionUnits(List<String> actionUnits) {
        this.actionUnits = actionUnits;
    }

    @Override
    public boolean check(Game game, Map<String, List<Targetable>> targets) {
        boolean result = game.getCurrentActionUnitsList().stream()
                .map(au -> au.getName())
                .collect(Collectors.toList()).containsAll(actionUnits);

        Logger.getGlobal().warning("ExecutedActionUnits: "+ result);
        return result;
    }
}
