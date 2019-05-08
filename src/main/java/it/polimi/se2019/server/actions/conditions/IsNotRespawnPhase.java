package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;

import java.util.List;
import java.util.Map;

public class IsNotRespawnPhase implements Condition {

    private String playerPhase;
    //TODO may need to implement a playerPhase if playerPhase class is necessary to manage a player turn.

    @Override
    public boolean check(Game game, Map<String, List<Targetable>> targets) {
        return false;
    }
}
