package it.polimi.se2019.server.actions.effects;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.RoomColor;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.Player;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DamageRoom extends Damage {

    public DamageRoom(Integer amount) {
        super(amount);
    }

    @Override
    public void run(Game game, Map<String, List<Targetable>> targets) {
        Tile tile = (Tile) targets.get("tile").get(0);
        RoomColor roomColor = tile.getColor();

        List<Player> targetList = game.getPlayerList().stream()
                .filter(p -> p.getCharacterState().getTile().getColor() == roomColor && p != game.getCurrentPlayer())
                .collect(Collectors.toList());

        targetList.stream()
                .forEach(p -> p.getCharacterState().addDamage(game.getCurrentPlayer().getColor(), super.amount));
    }
}
