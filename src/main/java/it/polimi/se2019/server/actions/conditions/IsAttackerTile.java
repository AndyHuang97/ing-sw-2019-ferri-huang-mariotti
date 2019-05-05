package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.Player;

public class IsAttackerTile implements Condition {

    private Tile tile;
    private Player attacker;

    public IsAttackerTile(Tile tile, Player attacker) {
        this.tile = tile;
        this.attacker = attacker;
    }

    @Override
    public boolean check() {
        return tile == attacker.getCharacterState().getTile();
    }
}
