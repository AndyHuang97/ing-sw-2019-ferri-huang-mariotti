package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.util.CommandConstants;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * This condition checks whether the selected tile is not the same as the attacker's tile.
 */
public class IsNotAttackerTile implements Condition {

    private static final int TILEPOSITION = 0;

    private boolean isTile;

    public IsNotAttackerTile(boolean isTile) {
        this.isTile = isTile;
    }

    @Override
    public boolean check(Game game, Map<String, List<Targetable>> targets) {
        Tile targetTile;
        if (isTile) {
            targetTile = (Tile) targets.get(CommandConstants.TILELIST).get(TILEPOSITION);
        } else {
            targetTile = ((Player)targets.get(CommandConstants.TARGETLIST).get(TILEPOSITION))
                    .getCharacterState().getTile();
        }

        Logger.getGlobal().info("IsNotAttackerTile: "+
                (targetTile != game.getCurrentPlayer().getCharacterState().getTile()));
        return targetTile != game.getCurrentPlayer().getCharacterState().getTile();
    }
}
