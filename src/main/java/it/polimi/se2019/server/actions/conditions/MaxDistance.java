package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;

import java.util.List;
import java.util.Map;

public class MaxDistance implements Condition {

    private Integer amount;

    public MaxDistance(Integer amount) {
        this.amount = amount;
    }

    @Override
    public boolean check(Game game, Map<String, List<Targetable>> targets) {
        Tile initialTile = (Tile) targets.get("tileList").get(0);
        Tile finalTile = (Tile) targets.get("tileList").get(1);
        return game.getBoard().generateGraph().isReachable(initialTile, finalTile, amount);
    }
}
