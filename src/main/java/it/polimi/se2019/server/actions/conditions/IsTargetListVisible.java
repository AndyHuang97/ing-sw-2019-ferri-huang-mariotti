package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.util.CommandConstants;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * This condition checks whether the target list is visible.
 *
 * @author andreahuang
 *
 */
public class IsTargetListVisible implements Condition {

    private static final int TARGETPOSITION = 0;

    private String actionUnitName;

    /**
     * Default constructor. It can set up the name of an action unit from which to retrieve the initial fron
     * which to calculate the visibility.
     *
     * @param actionUnitName
     */
    public IsTargetListVisible(String actionUnitName) {
        this.actionUnitName = actionUnitName;
    }

    /**
     * Checks whether the target list is visible.
     *
     * @param game the game on which to perform the evaluation.
     * @param targets the targets is the input of the current player. Either tiles or players.
     * @return true if target list is visible, false otherwise.
     */
    @Override
    public boolean check(Game game, Map<String, List<Targetable>> targets) {
        Tile initialTile;
        if (actionUnitName != null) {
            initialTile = ((Player)game.getActionUnitTargetList(actionUnitName).get(TARGETPOSITION))
                    .getCharacterState().getTile();
        }
        else {
            initialTile = game.getCurrentPlayer().getCharacterState().getTile();
        }

        List<Targetable> targetList = targets.get(CommandConstants.TARGETLIST);
        boolean result = true;

        // getVisibleTargets(game) would return the attacker too...
        initialTile.getVisibleTargets(game).remove(game.getCurrentPlayer());
        for (Targetable t : targetList) {
            if(!initialTile.getVisibleTargets(game).contains(t)) {
                result = false;
            }
        }

        Logger.getGlobal().warning("IsTargetListVisible: "+result);
        return result;
    }
}
