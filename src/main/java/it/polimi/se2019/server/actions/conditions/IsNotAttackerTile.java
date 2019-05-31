package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.util.ConditionConstants;

import java.util.List;
import java.util.Map;

/**
 * This condition checks whether the selected tile is not the same as the attacker's tile.
 */
public class IsNotAttackerTile implements Condition {

    private static final int TILEPOSITION = 0;

    @Override
    public boolean check(Game game, Map<String, List<Targetable>> targets) {
        List<Targetable> tileList = targets.get(ConditionConstants.TILE);
        Tile targetTile = (Tile) tileList.get(TILEPOSITION);

        return targetTile != game.getCurrentPlayer().getCharacterState().getTile();
    }
}
