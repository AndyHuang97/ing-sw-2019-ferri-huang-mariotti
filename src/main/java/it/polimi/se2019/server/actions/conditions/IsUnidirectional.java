package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.actions.Direction;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.util.CommandConstants;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class IsUnidirectional implements Condition {

    private static final int POSITION = 0;
    @Override
    public boolean check(Game game, Map<String, List<Targetable>> targets) {
        Tile attackerTile = game.getCurrentPlayer().getCharacterState().getTile();
        Tile firstTile = (Tile) targets.get(CommandConstants.TILELIST).get(POSITION);
        List<Tile> tileList = targets.get(CommandConstants.TILELIST).stream()
                .map(t->(Tile)t)
                .collect(Collectors.toList());
        Comparator<Tile> tileComparator = (t1, t2) -> {
            Integer d1 = game.getBoard().getTileTree().distance(attackerTile,t1);
            Integer d2 = game.getBoard().getTileTree().distance(attackerTile,t2);
            return d1.compareTo(d2);
        };
        tileList.sort(tileComparator);
        try {
            Direction dir = game.getBoard().getDirection(attackerTile, firstTile);
            Logger.getGlobal().info("Unidirectional: " +
                    game.getBoard().isOneDirectionList(dir, attackerTile, tileList));
            return game.getBoard().isOneDirectionList(dir, attackerTile, tileList);
        } catch (IllegalStateException e) {
            return false;
        }

    }

}
