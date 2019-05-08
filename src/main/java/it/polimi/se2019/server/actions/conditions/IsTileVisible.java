package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.Player;

import java.util.List;
import java.util.Map;

public class IsTileVisible implements Condition {

    private Tile tile;
    private Player attacker;

    public IsTileVisible(Tile tile, Player attacker) {
        this.tile = tile;
        this.attacker = attacker;
    }

    @Override
    public boolean check(Game game, Map<String, List<Targetable>> targets) {
        return false;
    }
}
