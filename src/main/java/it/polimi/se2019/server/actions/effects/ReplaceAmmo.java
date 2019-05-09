package it.polimi.se2019.server.actions.effects;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.player.AmmoColor;

import java.util.List;
import java.util.Map;

public class ReplaceAmmo implements Effect {

    private AmmoColor ammoColor;

    public ReplaceAmmo(AmmoColor ammoColor) {
        this.ammoColor = ammoColor;
    }

    @Override
    public void run(Game game, Map<String, List<Targetable>> targets) {

    }
}
