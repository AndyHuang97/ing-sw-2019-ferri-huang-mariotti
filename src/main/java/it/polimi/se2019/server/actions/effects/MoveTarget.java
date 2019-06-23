package it.polimi.se2019.server.actions.effects;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.util.CommandConstants;

import java.util.List;
import java.util.Map;

public class MoveTarget implements Effect {

    private static final int TILEPOSITION = 0;
    private static final int TARGETPOSITION = 0;

    private boolean self;
    private boolean isTile;

    public MoveTarget(boolean self, boolean isTile) {
        this.self = self;
        this.isTile = isTile;
    }

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
