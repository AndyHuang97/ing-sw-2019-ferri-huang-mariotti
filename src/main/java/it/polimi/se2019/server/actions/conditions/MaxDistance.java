package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.board.Tile;

public class MaxDistance implements Condition {

    private Tile initialPos, finalPos;
    private Integer amount;

    public MaxDistance(Tile initialPos, Tile finalPos, Integer amount) {
        this.initialPos = initialPos;
        this.finalPos = finalPos;
        this.amount = amount;
    }

    @Override
    public boolean check() {
        return false;
    }
}
