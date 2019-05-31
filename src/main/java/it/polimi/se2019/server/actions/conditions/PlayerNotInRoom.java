package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.RoomColor;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.util.ConditionConstants;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class PlayerNotInRoom implements Condition {

    private static final int ROOMCOLORPOSITION = 0;

    private Player attacker;
    private String Color;

    @Override
    public boolean check(Game game, Map<String, List<Targetable>> targets) {
        Player attacker = (Player) game.getCurrentPlayer();
        RoomColor roomColor = (RoomColor) targets.get(ConditionConstants.ROOMCOLOR).get(ROOMCOLORPOSITION);
        Tile[][] tileMap = game.getBoard().getTileMap();

        return Arrays.stream(tileMap)
                .allMatch(row -> Arrays.stream(row)
                        .filter(t -> t.getRoomColor() == roomColor)
                        .map(t -> t.getPlayers(game))
                        .noneMatch(lst -> lst.contains(attacker)));
    }
}
