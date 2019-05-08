package it.polimi.se2019.server.actions.effects;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;

import java.util.Map;

public class MarkTile implements Effect {

    private Tile tile;
    private Integer amount;

    @Override
    public void run(Game game, Map<String, Map<Targetable, Integer>> targets) {

    }
}
