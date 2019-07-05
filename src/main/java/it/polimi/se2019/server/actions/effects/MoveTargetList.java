package it.polimi.se2019.server.actions.effects;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.util.CommandConstants;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This effect moves all the targets to a tile
 *
 * @author FF
 *
 */
public class MoveTargetList implements Effect {

    private static final int TILE_POSITION = 0;

    private boolean self;
    private String actionUnitName;

    /**
     * Default construtctor. It sets up the self and isTile boolean parameters to interpret the input correctly.
     *
     * @param self is the boolean indicating whether the moving player is the attacker
     * @param actionUnitName
     */
    public MoveTargetList(boolean self, String actionUnitName) {
        this.self = self;
        this.actionUnitName = actionUnitName;
    }

    /**
     * Moves the targest to the correct tile
     *
     * @param game the game
     * @param targets the targets to move
     */
    @Override
    public void run(Game game, Map<String, List<Targetable>> targets) {
        Tile tile;

        if (actionUnitName == null) {
            if (self) {
                tile = game.getCurrentPlayer().getCharacterState().getTile();
            } else {
                tile = (Tile) targets.get(CommandConstants.TILELIST).get(TILE_POSITION);
            }
        } else {
            tile = (Tile) game.getCurrentActionUnitsList().stream()
                    .filter(au -> au.getName().equals(actionUnitName))
                    .findFirst().orElseThrow(IllegalStateException::new)
                    .getCommands().get(CommandConstants.TILELIST).get(TILE_POSITION);
        }

        List<Targetable> targetList = targets.get(CommandConstants.TARGETLIST);

        // stores old tiles
        List<Targetable> oldTileList = targetList.stream()
                .map(t -> (Player) t)
                .map(t -> t.getCharacterState().getTile())
                .collect(Collectors.toList());
        targets.put(CommandConstants.OLDTILELIST, oldTileList);

        // sets the new positions
        targetList.stream().forEach(p -> ((Player) p).getCharacterState().setTile(tile));




    }
}
