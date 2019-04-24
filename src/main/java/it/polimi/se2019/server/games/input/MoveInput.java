package it.polimi.se2019.server.games.input;

import it.polimi.se2019.server.games.board.Tile;

public class MoveInput extends Input {

    private Tile tile;

    public MoveInput(Tile tile) {
        super("M");
        this.tile = tile;
    }

    public Tile getTile() {
        return tile;
    }

    public void setTile(Tile tile) {
        this.tile = tile;
    }
}
