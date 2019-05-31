package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.util.ConditionConstants;

import java.util.List;
import java.util.Map;

public class IsTileVisible implements Condition {

    private static final int TILEPOSITION = 0;

    @Override
    public boolean check(Game game, Map<String, List<Targetable>> targets) {
        Targetable targetTile = targets.get(ConditionConstants.TILE).get(TILEPOSITION);
        Tile attackerTile = game.getCurrentPlayer().getCharacterState().getTile();
        return attackerTile.getVisibleTiles(game.getBoard()).contains(targetTile);
    }
}
