package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.util.CommandConstants;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * This condition checks whether the list of tiles is visible.
 *
 * @author andreahuang
 *
 */
public class IsTileListVisible implements Condition {

    /**
     * Checks whether the list of tiles is visible.
     *
     * @param game the game on which to perform the evaluation.
     * @param targets the targets is the input of the current player. Either tiles or players.
     * @return true if all the tiles are visible, false otherwise.
     */
    @Override
    public boolean check(Game game, Map<String, List<Targetable>> targets) {
        Tile attackerTile = game.getCurrentPlayer().getCharacterState().getTile();

        Logger.getGlobal().info("IsTileListVisible: "+
                targets.get(CommandConstants.TILELIST).stream()
                        .map(t -> (Tile) t)
                        .allMatch(tile -> attackerTile.getVisibleTiles(game.getBoard()).contains(tile)));
        return targets.get(CommandConstants.TILELIST).stream()
                .map(t -> (Tile) t)
                .allMatch(tile -> attackerTile.getVisibleTiles(game.getBoard()).contains(tile));
    }
}
