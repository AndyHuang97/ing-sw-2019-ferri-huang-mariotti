package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.RoomColor;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.util.CommandConstants;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * This condition checks whether the attacker is not in the selected room.
 *
 * @author andreahuang
 *
 */
public class PlayerNotInRoom implements Condition {

    private static final int TILE_POSITION = 0;

    /**
     * Checks whether the attacker is not in the selected room.
     *
     * @param game the game on which to perform the evaluation.
     * @param targets the targets is the input of the current player. Either tiles or players.
     * @return true if the attacker is not in the room, false otherwise.
     */
    @Override
    public boolean check(Game game, Map<String, List<Targetable>> targets) {
        Player attacker =  game.getCurrentPlayer();
        List<Tile> room = ((Tile)(targets.get(CommandConstants.TILELIST)
                .get(TILE_POSITION))).getRoom(game.getBoard());

        Logger.getGlobal().info("PlayerNotInRoom: "+
                !room.contains(attacker.getCharacterState().getTile()));
        return !room.contains(attacker.getCharacterState().getTile());
    }
}
