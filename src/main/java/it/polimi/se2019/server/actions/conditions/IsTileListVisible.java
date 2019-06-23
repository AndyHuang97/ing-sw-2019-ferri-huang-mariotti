package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.util.CommandConstants;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class IsTileListVisible implements Condition {

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
