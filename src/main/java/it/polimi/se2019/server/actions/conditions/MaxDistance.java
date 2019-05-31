package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.util.ConditionConstants;

import java.util.List;
import java.util.Map;

public class MaxDistance implements Condition {

    private static final int INITIALTILEPOSITION = 0;
    private static final int FINALTILEPOSITION = 1;

    private Integer amount;

    public MaxDistance(Integer amount) {
        this.amount = amount;
    }

    @Override
    public boolean check(Game game, Map<String, List<Targetable>> targets) {
        Tile initialTile = (Tile) targets.get(ConditionConstants.TILELIST).get(INITIALTILEPOSITION);
        Tile finalTile = (Tile) targets.get(ConditionConstants.TILELIST).get(FINALTILEPOSITION);
        return game.getBoard().getTileTree().isReachable(initialTile, finalTile, amount);
    }
}
