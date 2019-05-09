package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.RoomColor;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class PlayerNotInRoom implements Condition {

    private Player attacker;
    private String Color;

    @Override
    public boolean check(Game game, Map<String, List<Targetable>> targets) {
        Player attacker = game.getCurrentPlayer();
        RoomColor roomColor = (RoomColor) targets.get("roomColor").get(0);
        List<Tile> room = new ArrayList<>();
        Tile[][] tileMap = game.getBoard().getTileMap();

        return Arrays.stream(tileMap)
                .allMatch(row -> Arrays.stream(row)
                        .filter(t -> t.getColor() == roomColor)
                        .map(t -> t.getPlayers(game))
                        .allMatch(lst -> !lst.contains(attacker)));
    }
}
