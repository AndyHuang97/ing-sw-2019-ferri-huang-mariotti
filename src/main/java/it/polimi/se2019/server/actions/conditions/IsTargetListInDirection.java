package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.actions.Direction;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.util.CommandConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This condition checks whether the target list is in the chosen direction.
 *
 * @author andreahuang
 *
 */
public class IsTargetListInDirection implements Condition {

    private static final int POSITION = 0;

    /**
     * Checks whether the target list is in the chosen direction.
     *
     * @param game the game on which to perform the evaluation.
     * @param targets the targets is the input of the current player. Either tiles or players.
     * @return true if all targets are in direction, false otherwise.
     */
    @Override
    public boolean check(Game game, Map<String, List<Targetable>> targets) {

        Tile attackerTile = game.getCurrentPlayer().getCharacterState().getTile();
        Tile firstTile = (Tile) targets.get(CommandConstants.TILELIST).get(POSITION);
        List<Player> targetList = targets.get(CommandConstants.TARGETLIST).stream()
                .map(t->(Player)t)
                .collect(Collectors.toList());
        try {
            Direction dir = game.getBoard().getDirection(attackerTile, firstTile);

            return targetList.stream()
                    .map(p -> p.getCharacterState().getTile())
                    .allMatch(t -> dir.equals(game.getBoard().getDirection(attackerTile, t)));
        } catch (IllegalStateException e) {
            return false;
        }

    }
}
