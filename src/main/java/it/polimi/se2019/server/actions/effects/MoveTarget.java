package it.polimi.se2019.server.actions.effects;

import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.Player;

public class MoveTarget implements Effect {

    private Tile tile;
    private Player player;

    public MoveTarget(Tile tile, Player player) {
        this.tile = tile;
        this.player = player;
    }

    @Override
    public void run() {
        player.getCharacterState().setTile(tile);
    }
}
