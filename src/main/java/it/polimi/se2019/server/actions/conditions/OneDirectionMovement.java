package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;

import java.util.List;
import java.util.Map;

public class OneDirectionMovement implements Condition {

    private Tile initialPos, finalPos;

    public OneDirectionMovement(Tile initialPos, Tile finalPos) {
        this.initialPos = initialPos;
        this.finalPos = finalPos;
    }

    @Override
    public boolean check(Game game, Map<String, List<Targetable>> targets) {
        return false;
    }
}
