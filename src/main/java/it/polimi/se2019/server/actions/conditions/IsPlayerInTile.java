package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.Player;

public class IsPlayerInTile  implements Condition {

    private Tile tile;
    private Player player;

    public IsPlayerInTile(Tile tile, Player player) {
        this.tile = tile;
        this.player = player;
    }

    @Override
    public boolean check() {
        return false;
    }
}
