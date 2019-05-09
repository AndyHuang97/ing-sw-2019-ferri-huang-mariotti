package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.Player;

import java.util.List;
import java.util.Map;

public class TargetListDistance implements Condition {

    private Integer amount;

    public TargetListDistance(Integer amount) {
        this.amount = amount;
    }

    @Override
    public boolean check(Game game, Map<String, List<Targetable>> targets) {
        Tile attackerTile = game.getCurrentPlayer().getCharacterState().getTile();

        List<Targetable> targetList = targets.get("targetList");
        boolean result = true;

        targetList.stream()
                .forEach(t -> ((Player)t).getCharacterState().getTile());
        return result;
    }
}
