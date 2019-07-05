package it.polimi.se2019.server.actions.effects;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.util.CommandConstants;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * This effect sorts the tiles based on the current player tile
 *
 * @author FF
 *
 */
public class SortTile implements Effect {

    /**
     * Sorts the tiles based on the current player tile
     *
     * @param game the game
     * @param targets the target is the tile list
     */
    @Override
    public void run(Game game, Map<String, List<Targetable>> targets) {
        List<Tile> tileList = targets.get(CommandConstants.TILELIST).stream()
                .map(t -> (Tile) t).collect(Collectors.toList());

        Tile baseTile = game.getCurrentPlayer().getCharacterState().getTile();

        Comparator<Tile> tileComparator = (t1,t2) -> {
            Integer d1 = game.getBoard().getTileTree().distance(baseTile,t1);
            Integer d2 = game.getBoard().getTileTree().distance(baseTile,t2);
            return d1.compareTo(d2);
        };
        Logger.getGlobal().info("Sorting...");
        tileList.sort(tileComparator);
        targets.put(CommandConstants.TILELIST, tileList.stream()
                .map(t->(Targetable)t).collect(Collectors.toList()));
    }
}
