package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.Player;

import java.util.List;
import java.util.Map;

public class IsTargetListVisible implements Condition {

    private Tile tile;
    private List<Player> targetList;

    public IsTargetListVisible(Tile tile, List<Player> targetList) {
        this.tile = tile;
        this.targetList = targetList;
    }

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
