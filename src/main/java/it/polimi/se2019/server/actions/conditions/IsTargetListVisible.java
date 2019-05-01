package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.Player;

import java.util.List;

public class IsTargetListVisible implements Condition {

    private Tile tile;
    private List<Player> targetList;

    public IsTargetListVisible(Tile tile, List<Player> targetList) {
        this.tile = tile;
        this.targetList = targetList;
    }

    @Override
    public boolean check() {
        return false;
    }
}
