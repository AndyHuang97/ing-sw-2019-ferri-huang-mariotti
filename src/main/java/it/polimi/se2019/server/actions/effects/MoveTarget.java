package it.polimi.se2019.server.actions.effects;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.Player;

import java.util.List;
import java.util.Map;

public class MoveTarget implements Effect {

    @Override
    public void run(Game game, Map<String, List<Targetable>> targets) {
        Player target = (Player) targets.get("target").get(0);
        Tile tile = (Tile) targets.get("tile").get(0);

        target.getCharacterState().setTile(tile);
    }
}
