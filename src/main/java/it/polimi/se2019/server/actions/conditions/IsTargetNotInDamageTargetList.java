package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.player.PlayerColor;

import java.util.List;
import java.util.Map;

/**
 * This condition checks whether the target was not in the damage target list of a shoot action.
 *
 * @author andreahuang
 *
 */
public class IsTargetNotInDamageTargetList implements Condition {

    /**
     * Checks whether the target was not in the damage target list of a shoot action.
     *
     * @param game the game on which to perform the evaluation.
     * @param targets the targets is the input of the current player. Either tiles or players.
     * @return true if not in damage target list, false otherwise.
     */
    @Override
    public boolean check(Game game, Map<String, List<Targetable>> targets) {
        Condition condition = new IsTargetInDamageTargetList();
        return !condition.check(game, targets);
    }
}
