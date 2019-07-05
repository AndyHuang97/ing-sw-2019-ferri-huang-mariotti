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
 * This condition checks whether the target list is not visible.
 *
 * @author andreahuang
 *
 */
public class IsTargetListNotVisible implements Condition {

    /**
     * Checks whether the target list is not visible.
     *
     * @param game the game on which to perform the evaluation.
     * @param targets the targets is the input of the current player. Either tiles or players.
     * @return true if not visible, false otherwise.
     */
    @Override
    public boolean check(Game game, Map<String, List<Targetable>> targets) {
        Tile attackerTile = game.getCurrentPlayer().getCharacterState().getTile();

        Logger.getGlobal().info("IsTargetListNotVisible: "+
                targets.get(CommandConstants.TARGETLIST).stream()
                        .map(t -> (Player) t)
                        .allMatch(targetPlayer -> !attackerTile.getVisibleTargets(game).contains(targetPlayer)));
        return targets.get(CommandConstants.TARGETLIST).stream()
                .map(t -> (Player) t)
                .allMatch(targetPlayer -> !attackerTile.getVisibleTargets(game).contains(targetPlayer));
    }
}
