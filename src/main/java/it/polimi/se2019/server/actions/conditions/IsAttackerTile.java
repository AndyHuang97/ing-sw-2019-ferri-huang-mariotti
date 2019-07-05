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
 * This condition checks whether the selected tile is the same as the attacker's tile.
 *
 * @author andreahuang
 */
public class IsAttackerTile implements Condition {

    private static final int TILEPOSITION = 0;

    private boolean isTile;

    /**
     * Default constructor.
     *
     * @param isTile boolean indicating whether the input to check is a tile of a player target
     */
    public IsAttackerTile(boolean isTile) {
        this.isTile = isTile;
    }

    /**
     * Checks whether the input is on the attacker tile.
     *
     * @param game the game on which to perform the evaluation.
     * @param targets the targets is the input of the current player. Either tiles or players.
     * @return true if it is on attacker tile, false otherwise.
     */
    @Override
    public boolean check(Game game, Map<String, List<Targetable>> targets) {
        Tile targetTile;
        if (isTile) {
            targetTile = (Tile) targets.get(CommandConstants.TILELIST).get(TILEPOSITION);
        } else {
            targetTile = ((Player)targets.get(CommandConstants.TARGETLIST).get(TILEPOSITION))
                    .getCharacterState().getTile();
        }

        Logger.getGlobal().info("IsAttackerTile:"+
                (targetTile == game.getCurrentPlayer().getCharacterState().getTile()));
        return targetTile == game.getCurrentPlayer().getCharacterState().getTile();
    }
}
