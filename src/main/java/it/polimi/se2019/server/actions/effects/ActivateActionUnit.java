package it.polimi.se2019.server.actions.effects;

import it.polimi.se2019.server.actions.ActionUnit;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;

import java.util.List;
import java.util.Map;

public class ActivateActionUnit implements Effect {

    private ActionUnit actionUnit;

    public ActivateActionUnit(ActionUnit actionUnit) {
        this.actionUnit = actionUnit;
    }

    @Override
    public void run(Game game, Map<String, List<Targetable>> targets) {

    }
}
