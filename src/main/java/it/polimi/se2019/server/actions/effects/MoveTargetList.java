package it.polimi.se2019.server.actions.effects;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.Player;

import java.util.List;
import java.util.Map;

public class MoveTargetList implements Effect {

    @Override
    public void run(Game game, Map<String, List<Targetable>> targets) {
        List<Targetable> targetList = targets.get("targetList");
        Tile tile = (Tile) targets.get("tile").get(0);

        targetList.stream().forEach(p -> ((Player) p).getCharacterState().setTile(tile));
    }
}
