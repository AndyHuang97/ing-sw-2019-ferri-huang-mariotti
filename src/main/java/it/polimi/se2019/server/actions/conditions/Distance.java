package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.util.CommandConstants;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * This condition checks whether the distance between the attacker and the target tile is exactly the same as the
 * member variable amount.
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

    public Distance(Integer amount, boolean self, boolean finalIsTile, boolean initialIsTile, String actionUnitName) {
        this.amount = amount;
        this.self = self;
        this.finalIsTile = finalIsTile;
        this.initialIsTile = initialIsTile;
        this.actionUnitName = actionUnitName;
    }

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

    protected Tile getInitialTile(Game game, Map<String, List<Targetable>> targets) {
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

    protected List<Tile> getFinalTile(Map<String, List<Targetable>> targets) {
        if (finalIsTile) {
            if (targets.get(CommandConstants.TILELIST).size() == ONE_TILE_LIST){ // if only 1 tile was selected
                return Arrays.asList((Tile) targets.get(CommandConstants.TILELIST).get(FINAL_TILE_POSITION_SELF));
            }else {
                return Arrays.asList((Tile) targets.get(CommandConstants.TILELIST).get(FINAL_TILE_POSITION_NO_SELF));
            }
        } else {
            return targets.get(CommandConstants.TARGETLIST).stream()
                    .map(t -> ((Player) t).getCharacterState().getTile())
                    .collect(Collectors.toList());
        }
    }
}
