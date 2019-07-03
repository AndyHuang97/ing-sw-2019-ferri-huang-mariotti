package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.actions.Direction;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.util.CommandConstants;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class IsUnidirectional implements Condition {

    private static final int POSITION = 0;
    private boolean self;
    @Override
    public boolean check(Game game, Map<String, List<Targetable>> targets) {
        
        Tile targetTile;
        if (self) {
            targetTile = game.getCurrentPlayer().getCharacterState().getTile();
        }
        else {
            targetTile = ((Player) targets.get(CommandConstants.TARGETLIST).get(POSITION)).getCharacterState().getTile();
        }
        Tile firstTile = (Tile) targets.get(CommandConstants.TILELIST).get(POSITION);
        List<Tile> tileList = targets.get(CommandConstants.TILELIST).stream()
                .map(t->(Tile)t)
                .collect(Collectors.toList());
        Comparator<Tile> tileComparator = (t1, t2) -> {
            Integer d1 = game.getBoard().getTileTree().distance(targetTile,t1);
            Integer d2 = game.getBoard().getTileTree().distance(targetTile,t2);
            return d1.compareTo(d2);
        };
        tileList.sort(tileComparator);
        try {
            Direction dir = game.getBoard().getDirection(targetTile, firstTile);
            Logger.getGlobal().info("Unidirectional: " +
                    game.getBoard().isOneDirectionList(dir, targetTile, tileList));
            return game.getBoard().isOneDirectionList(dir, targetTile, tileList);
        } catch (IllegalStateException e) {
            Logger.getGlobal().info("Not allowed direction");
            return false;
        }

    }

}
