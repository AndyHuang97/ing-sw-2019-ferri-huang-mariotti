package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.util.CommandConstants;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class IsTargetListNotVisible implements Condition {

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
