package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;

import java.util.List;
import java.util.Map;

/**
 * This condition checks whether the selected tile is the same as the attacker's tile
 */
public class IsAttackerTile implements Condition {

    @Override
    public boolean check(Game game, Map<String, List<Targetable>> targets) {
        List<Targetable> tileList = targets.get("tile");
        Tile targetTile = (Tile) tileList.get(0);

        return targetTile == game.getCurrentPlayer().getCharacterState().getTile();
    }
}
