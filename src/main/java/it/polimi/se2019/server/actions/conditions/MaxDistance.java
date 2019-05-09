package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;

import java.util.List;
import java.util.Map;

public class MaxDistance implements Condition {

    private Tile initialPos;
    private Tile finalPos;
    private Integer amount;

    public MaxDistance(Tile initialPos, Tile finalPos, Integer amount) {
        this.initialPos = initialPos;
        this.finalPos = finalPos;
        this.amount = amount;
    }

    @Override
    public boolean check(Game game, Map<String, List<Targetable>> targets) {
        return false;
    }
}
