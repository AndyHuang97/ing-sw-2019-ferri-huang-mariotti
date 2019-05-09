package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;

import java.util.List;
import java.util.Map;

public class IsTargetListVisible implements Condition {

    @Override
    public boolean check(Game game, Map<String, List<Targetable>> targets) {
        List<Targetable> targetList = targets.get("targetList");
        Tile attackerTile = game.getCurrentPlayer().getCharacterState().getTile();
        boolean result = true;

        for (Targetable t : targetList) {
            if(!attackerTile.getVisibleTargets(game).contains(t)) {
                result = false;
            }
        }

        return result;
    }
}
