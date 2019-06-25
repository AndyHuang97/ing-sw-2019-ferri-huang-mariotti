package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.util.CommandConstants;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class OneTileOneTarget implements Condition {

    @Override
    public boolean check(Game game, Map<String, List<Targetable>> targets) {

        List<Tile> tileList = targets.get(CommandConstants.TILELIST).stream()
                .map(t->(Tile)t).collect(Collectors.toList());
        List<Player> targetList = targets.get(CommandConstants.TARGETLIST).stream()
                .map(t->(Player)t).collect(Collectors.toList());


        Logger.getGlobal().info("OneTileOneTarget: "+
                (targetList.stream()
                        .allMatch(p -> tileList.stream().anyMatch(t -> t.getPlayers(game).contains(p)))
                &&
                targetList.stream()
                        .map(p -> p.getCharacterState().getTile())
                        .allMatch(new HashSet<>()::add)));
        return targetList.stream()
                .allMatch(p -> tileList.stream().anyMatch(t -> t.getPlayers(game).contains(p)))
                &&
                targetList.stream()
                        .map(p -> p.getCharacterState().getTile())
                        .allMatch(new HashSet<>()::add);
    }
}
