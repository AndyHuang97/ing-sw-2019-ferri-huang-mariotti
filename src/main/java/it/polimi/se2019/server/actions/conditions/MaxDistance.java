package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.exceptions.TileNotFoundException;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.util.CommandConstants;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MaxDistance extends Distance {

    private static final int TILE_POSITION=0;


    public MaxDistance(Integer amount, boolean self, boolean finalIsTile, boolean initialIsTile, String actionUnitName) {
        super(amount, self, finalIsTile, initialIsTile, actionUnitName);
    }

    @Override
    public boolean check(Game game, Map<String, List<Targetable>> targets) {
        Tile initialTile;
        if (super.actionUnitName == null) {
            initialTile = super.getInitialTile(game, targets);
        } else {
            initialTile = (Tile) game.getCurrentActionUnitsList().stream()
                    .filter(au -> au.getName().equals(super.actionUnitName))
                    .findFirst().orElseThrow(IllegalStateException::new)
                    .getCommands().get(CommandConstants.TILELIST).get(TILE_POSITION);
        }
        List<Tile> finalTileList = super.getFinalTile(targets);

        /*
        System.out.println(game.getBoard().getTileTree().isReachable(initialTile, finalTileList.get(0), amount));
        try {
            System.out.println(game.getBoard().getTilePosition(initialTile)[0] + ","+ game.getBoard().getTilePosition(initialTile)[0]);
            System.out.println(game.getBoard().getTilePosition(finalTileList.get(0))[0] + ","+ game.getBoard().getTilePosition(finalTileList.get(0))[1]);
            System.out.println(game.getBoard().getTilePosition(finalTileList.get(1))[0] + "," +game.getBoard().getTilePosition(finalTileList.get(1))[1]);
        }catch(TileNotFoundException e) {
            Logger.getGlobal().warning(e.toString());
        }

         */
        Logger.getGlobal().log(Level.INFO,"MaxDistance: {0}",
                finalTileList.stream()
                        .allMatch(finalTile -> game.getBoard().getTileTree().isReachable(initialTile, finalTile, amount)));
        Logger.getGlobal().info("initial tile: "+initialTile + "\tfinal tile: "+ finalTileList
                + "\tamount: " + amount);
        return finalTileList.stream()
                .allMatch(finalTile -> game.getBoard().getTileTree().isReachable(initialTile, finalTile, amount));
    }
}
