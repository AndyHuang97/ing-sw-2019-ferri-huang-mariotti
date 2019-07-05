package it.polimi.se2019.server.actions.effects;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.util.CommandConstants;

import java.util.List;
import java.util.Map;

/**
 * This effect moves a single target to a new position.
 *
 * @author andreahuang
 *
 */
public class MoveTarget implements Effect {

    private static final int TILEPOSITION = 0;
    private static final int TARGETPOSITION = 0;

    private boolean self;
    private boolean isTile;

    /**
     * Default construtctor. It sets up the self and isTile boolean parameters to interpret the input correctly.
     *
     * @param self is the boolean indicating whether the moving player is the attacker
     * @param isTile
     */
    public MoveTarget(boolean self, boolean isTile) {
        this.self = self;
        this.isTile = isTile;
    }

    /**
     * Moves the target to the correct tile
     *
     * @param game the game
     * @param targets the target to move
     */
    @Override
    public void run(Game game, Map<String, List<Targetable>> targets) {
        Player target;
        if (self) {
            target = game.getCurrentPlayer();
        }
        else {
            target = (Player) targets.get(CommandConstants.TARGETLIST).get(TARGETPOSITION);
        }
        Tile tile;
        if (isTile) {
            tile = (Tile) targets.get(CommandConstants.TILELIST).get(TILEPOSITION);
        } else {
            tile = ((Player) targets.get(CommandConstants.TARGETLIST).get(TARGETPOSITION))
                    .getCharacterState().getTile();
        }

        target.getCharacterState().setTile(tile);
    }
}
