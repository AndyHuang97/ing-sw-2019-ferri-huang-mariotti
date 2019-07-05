package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.util.CommandConstants;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * This condition checks whether the selected targets' positions are all different.
 *
 * @author andreahuang
 */
public class TargetListOnDifferentTiles implements Condition {

    /**
     * Checks whether the selected targets' positions are all different.
     *
     * @param game the game on which to perform the evaluation.
     * @param targets the targets is the input of the current player. Either tiles or players.
     * @return true if all targets are on different tiles, false otherwise.
     */
    @Override
    public boolean check(Game game, Map<String, List<Targetable>> targets) {
        List<Targetable> targetList = targets.get(CommandConstants.TARGETLIST);
        List<Tile> tileList =
                targetList.stream()
                        .map(t -> ((Player)t).getCharacterState().getTile())
                        .collect(Collectors.toList());

        Logger.getGlobal().info("TargetListOnDifferentTiles: "+
                tileList.stream().allMatch(new HashSet<>()::add));
        return tileList.stream().allMatch(new HashSet<>()::add);
    }
}
