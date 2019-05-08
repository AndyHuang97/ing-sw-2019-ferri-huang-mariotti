package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.RoomColor;
import it.polimi.se2019.server.games.player.Player;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class IsRoomVisible implements Condition {

    @Override
    public boolean check(Game game, Map<String, List<Targetable>> targets) {
        List<Targetable> roomColor = targets.get("roomColor");
        RoomColor targetRoom = (RoomColor)roomColor.get(0);
        Player attacker = game.getCurrentPlayer();
        return attacker.getCharacterState().getTile().getVisibleTiles(game.getBoard()).stream()
                    .map(t -> t.getColor())
                    .collect(Collectors.toList()).contains(targetRoom);
    }
}
