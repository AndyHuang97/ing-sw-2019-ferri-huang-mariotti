package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.util.CommandConstants;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This condition checks whether the player target is present in the cumulative damage target set, which
 * contains players who received damage in a single shoot.
 *
 * @author andreahuang
 *
 */
public class IsTargetInDamageTargetList implements Condition {

    private static final int PLAYERPOSITION = 0;

    /**
     * Checks whether the player target is present in the cumulative damage target set, which
     * contains players who received damage in a single shoot.
     *
     * @param game the game on which to perform the evaluation.
     * @param targets the targets is the input of the current player. Either tiles or players.
     * @return true if the player was in the cumulative damage target set, false otherwise.
     */
    @Override
    public boolean check(Game game, Map<String, List<Targetable>> targets) {
        Targetable targetPlayer = targets.get(CommandConstants.TARGETLIST).get(PLAYERPOSITION);
        Set<Targetable> damageTargetList = game.getCumulativeDamageTargetSet();
        return damageTargetList.contains(targetPlayer);
    }
}
