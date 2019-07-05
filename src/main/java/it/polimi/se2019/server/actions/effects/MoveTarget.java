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
     * Default constructor. It sets up the self and isTile boolean parameters to interpret the input correctly.
     *
     * @param self is the boolean indicating whether the moving player is the attacker
     * @param isTile
     */
    public MoveTarget(boolean self, boolean isTile) {
        this.self = self;
        this.isTile = isTile;
    }

    /**
     * This method moves a single target, interpreting the self and isTile boolean parameter.
     * If self is true, then it moves the attacker. The isTile parameter is true then it gets the final
     * tile from the tile input, otherwise it gets it from the target input tile.
     *
     * @param  game the game on which to perform the effect.
     * @param targets the targets is the input of the current player. Either tiles or players.
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
