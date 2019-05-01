package it.polimi.se2019.server.actions.effects;

import it.polimi.se2019.server.games.board.Tile;

public class DamageTile implements Effect {

    private Tile tile;
    private Integer amount;

    public DamageTile(Tile tile, Integer amount) {
        this.tile = tile;
        this.amount = amount;
    }

    @Override
    public void run() {

    }
}
