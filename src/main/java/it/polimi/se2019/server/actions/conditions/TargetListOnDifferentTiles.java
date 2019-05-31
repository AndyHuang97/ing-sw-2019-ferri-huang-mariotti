package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.util.CommandConstants;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TargetListOnDifferentTiles implements Condition {
    @Override
    public boolean check(Game game, Map<String, List<Targetable>> targets) {
        List<Targetable> targetList = targets.get(CommandConstants.TARGETLIST);
        List<Tile> tileList =
                targetList.stream()
                        .map(t -> ((Player)t).getCharacterState().getTile())
                        .collect(Collectors.toList());

        return tileList.stream().allMatch(new HashSet<>()::add);
    }
}
