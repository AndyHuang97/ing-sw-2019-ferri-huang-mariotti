package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.util.CommandConstants;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * This condition checks whether a room is visible from the current player's position.
 */
public class IsRoomVisible implements Condition {

    private static final int TILEPOSITION = 0;

    @Override
    public boolean check(Game game, Map<String, List<Targetable>> targets) {
        Tile roomTile  = (Tile) targets.get(CommandConstants.TILELIST).get(TILEPOSITION);
        Player attacker = game.getCurrentPlayer();
        Logger.getGlobal().info("IsRoomVisible: "+
                attacker.getCharacterState().getTile().getVisibleTiles(game.getBoard()).
                        contains(roomTile));
        return attacker.getCharacterState().getTile().getVisibleTiles(game.getBoard()).
                contains(roomTile);
    }
}
