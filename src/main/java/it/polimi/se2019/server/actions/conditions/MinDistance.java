package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class MinDistance extends Distance {

    public MinDistance(Integer amount, boolean self, boolean finalIsTile, boolean initialIsTile, String actionUnitName) {
        super(amount, self, finalIsTile, initialIsTile, actionUnitName);
    }

    @Override
    public boolean check(Game game, Map<String, List<Targetable>> targets) {
        Tile initialTile = super.getInitialTile(game, targets);
        List<Tile> finalTileList = super.getFinalTile(targets);


        Logger.getGlobal().info("MinDistance: "+
                finalTileList.stream()
                        .allMatch(finalTile -> game.getBoard().getTileTree().distance(initialTile, finalTile) >= amount));
        Logger.getGlobal().info("initial tile: "+initialTile + "\tfinal tile: "+ finalTileList
                + "\tamount: " + amount);
        return finalTileList.stream()
                .allMatch(finalTile -> game.getBoard().getTileTree().distance(initialTile, finalTile) >= amount);
    }
}
