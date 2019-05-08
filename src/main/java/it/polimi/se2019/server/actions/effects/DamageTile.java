package it.polimi.se2019.server.actions.effects;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;

import java.util.Map;

public class DamageTile implements Effect {

    private Tile tile;
    private Integer amount;

    public DamageTile(Tile tile, Integer amount) {
        this.tile = tile;
        this.amount = amount;
    }

    @Override
    public void run(Game game, Map<String, Map<Targetable, Integer>> targets) {

    }
}
