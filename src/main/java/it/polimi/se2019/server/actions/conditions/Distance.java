package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.util.CommandConstants;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * This condition checks whether the distance between the attacker and the target tile is exactly the same as the
 * member variable amount.
 *
 * @author andreahuang
 */
public class Distance implements Condition {

    private static final int INITIAL_TILE_POSITION = 0;
    private static final int FINAL_TILE_POSITION_SELF = 0;
    private static final int FINAL_TILE_POSITION_NO_SELF = 1;
    private static final int ONE_TILE_LIST = 1;

    protected Integer amount;
    private boolean self;
    private boolean finalIsTile;
    private boolean initialIsTile;
    private boolean lazy;
    protected String actionUnitName;

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
    public Distance(Integer amount, boolean self, boolean finalIsTile, boolean initialIsTile, boolean lazy, String actionUnitName) {
        this.amount = amount;
        this.self = self;
        this.finalIsTile = finalIsTile;
        this.initialIsTile = initialIsTile;
        this.lazy = lazy;
        this.actionUnitName = actionUnitName;
    }

    /**
     * Checks whether the distance between a first tile and a final tile is the same as the amount specified.
     *
     * @param game the game on which to perform the evaluation.
     * @param targets the targets is the input of the current player. Either tiles or players.
     * @return true if distance is the same, false otherwise.
     */
    @Override
    public boolean check(Game game, Map<String, List<Targetable>> targets) {

        Tile initialTile = this.getInitialTile(game, targets);
        List<Tile> tileList;
        if (finalIsTile) {
            tileList = targets.get(CommandConstants.TILELIST).stream()
                    .map(t -> (Tile) t).collect(Collectors.toList());
        } else {
            tileList = targets.get(CommandConstants.TARGETLIST).stream()
                    .map(t -> (Player) t).map(p -> p.getCharacterState().getTile())
                    .collect(Collectors.toList());
        }

        if (lazy) {
            Logger.getGlobal().info("Distance: "+
                    tileList.stream()
                            .anyMatch(t ->
                                    amount.equals(game.getBoard().getTileTree().distance(initialTile, t))));
            return tileList.stream()
                    .anyMatch(t ->
                            amount.equals(game.getBoard().getTileTree().distance(initialTile, t)));
        } else {

            Logger.getGlobal().info("Distance: "+
                    tileList.stream()
                            .allMatch(t ->
                                    amount.equals(game.getBoard().getTileTree().distance(initialTile, t))));
            return tileList.stream()
                    .allMatch(t ->
                            amount.equals(game.getBoard().getTileTree().distance(initialTile, t)));
        }
    }

    /**
     * The getInitialTile method evaluates the boolean parameters self and initialIsTile and retrieves
     * the first tile correctly.
     *
     * @param game the game on which to perform the evaluation.
     * @param targets the targets is the input of the current player. Either tiles or players.
     * @return the initial tile on which to perform the check.
     */
    Tile getInitialTile(Game game, Map<String, List<Targetable>> targets) {
        if (self) {
            return game.getCurrentPlayer().getCharacterState().getTile();
        } else {
            if (initialIsTile) {
                return (Tile) targets.get(CommandConstants.TILELIST).get(INITIAL_TILE_POSITION);
            } else {
                return ((Player) targets.get(CommandConstants.TARGETLIST).get(INITIAL_TILE_POSITION))
                        .getCharacterState().getTile();
            }
        }
    }

    /**
     * The getFinalTile method evaluates the boolean parameters self and initialIsTile and retrieves
     * the final tile list correctly.
     *
     * @param targets the targets is the input of the current player. Either tiles or players.
     * @return the final tile list.
     */
    List<Tile> getFinalTile(Map<String, List<Targetable>> targets) {
        if (finalIsTile) {
            if (targets.get(CommandConstants.TILELIST).size() == ONE_TILE_LIST){ // if only 1 tile was selected
                return Collections.singletonList((Tile) targets.get(CommandConstants.TILELIST).get(FINAL_TILE_POSITION_SELF));
            }else {
                return Collections.singletonList((Tile) targets.get(CommandConstants.TILELIST).get(FINAL_TILE_POSITION_NO_SELF));
            }
        } else {
            return targets.get(CommandConstants.TARGETLIST).stream()
                    .map(t -> ((Player) t).getCharacterState().getTile())
                    .collect(Collectors.toList());
        }
    }
}
