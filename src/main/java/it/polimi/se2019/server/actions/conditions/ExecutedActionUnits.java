package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;

import java.util.List;
import java.util.Map;
// TODO implement it
public class ExecutedActionUnits implements Condition {

    @Override
    public boolean check(Game game, Map<String, List<Targetable>> targets) {
        return false;
    }
}