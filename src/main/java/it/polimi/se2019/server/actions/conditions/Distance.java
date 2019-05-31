package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.util.ConditionConstants;

import java.util.List;
import java.util.Map;

/**
 * This condition checks whether the distance between the attacker and the target tile is exactly the same as the
 * member variable amount.
 */
public class Distance implements Condition {

    private static final int TILEPOSITION = 0;

    private Integer amount;

    public Distance(Integer amount) {
        this.amount = amount;
    }

    @Override
    public boolean check(Game game, Map<String, List<Targetable>> targets) {
        Tile attackerTile = game.getCurrentPlayer().getCharacterState().getTile();
        List<Targetable> tileList = targets.get(ConditionConstants.TILELIST);
        Tile targetTile = (Tile) tileList.get(TILEPOSITION);

        return amount.equals(game.getBoard().getTileTree().distance(attackerTile, targetTile));
    }
}
