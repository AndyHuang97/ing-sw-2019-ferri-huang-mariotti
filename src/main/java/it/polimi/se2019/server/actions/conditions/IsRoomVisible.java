package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.RoomColor;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.util.CommandConstants;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This condition checks whether a room is visible from the current player's position.
 */
public class IsRoomVisible implements Condition {

    private static final int ROOMCOLORPOSITIONINLIST = 0;

    @Override
    public boolean check(Game game, Map<String, List<Targetable>> targets) {
        List<Targetable> roomColor = targets.get(CommandConstants.ROOMCOLOR);
        RoomColor targetRoom = (RoomColor)roomColor.get(ROOMCOLORPOSITIONINLIST);
        Player attacker = (Player) game.getCurrentPlayer();
        return attacker.getCharacterState().getTile().getVisibleTiles(game.getBoard()).stream()
                    .map(Tile::getRoomColor)
                    .collect(Collectors.toList()).contains(targetRoom);
    }
}
