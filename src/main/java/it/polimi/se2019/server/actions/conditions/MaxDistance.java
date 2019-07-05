package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.util.CommandConstants;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This condition checks whether the distance between the attacker and the target tile is less than the amount
 * member variable.
 *
 * @author andreahuang
 *
 */
public class MaxDistance extends Distance {

    private static final int TILE_POSITION=0;

    /**
     * Default constructor. Sets up all parameters for check evaluation.
     *
     *  @param amount is an intenger indicating the distance
     * @param self boolean indicating whether the first tile to consider is the attacker or not
     * @param finalIsTile boolean indicating whether the last tile is calculated from a tile input or player target input.
     * @param initialIsTile boolean indicating whether the last tile is calculated from a tile input or player target input.
     * @param lazy when evaluating a list of destination tile list, if true only one tile needs to meet the check,
     *             otherwise all tiles need to meet the check
     * @param actionUnitName the name of the action unit from which
     */
    public MaxDistance(Integer amount, boolean self, boolean finalIsTile, boolean initialIsTile, boolean lazy, String actionUnitName) {
        super(amount, self, finalIsTile, initialIsTile, lazy, actionUnitName);
    }

    /**
     * Checks whether the distance between a first tile and a final tile is less than the specified amount.
     *
     * @param game the game on which to perform the evaluation.
     * @param targets the targets is the input of the current player. Either tiles or players.
     * @return true if distance is less, false otherwise.
     */
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

        Logger.getGlobal().log(Level.INFO,"MaxDistance: {0}",
                finalTileList.stream()
                        .allMatch(finalTile -> game.getBoard().getTileTree().isReachable(initialTile, finalTile, amount)));
        Logger.getGlobal().info("initial tile: "+initialTile + "\tfinal tile: "+ finalTileList
                + "\tamount: " + amount);
        return finalTileList.stream()
                .allMatch(finalTile -> game.getBoard().getTileTree().isReachable(initialTile, finalTile, amount));
    }
}
