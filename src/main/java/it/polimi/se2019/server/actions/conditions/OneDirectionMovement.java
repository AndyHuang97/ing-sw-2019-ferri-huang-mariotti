package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.board.Tile;

public class OneDirectionMovement implements Condition {

    private Tile initialPos, finalPos;

    public OneDirectionMovement(Tile initialPos, Tile finalPos) {
        this.initialPos = initialPos;
        this.finalPos = finalPos;
    }

    @Override
    public boolean check() {
        return false;
    }
}
