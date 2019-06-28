package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.util.CommandConstants;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class IsTargetInDamageTargetList implements Condition {

    private static final int PLAYERPOSITION = 0;

    @Override
    public boolean check(Game game, Map<String, List<Targetable>> targets) {
        Targetable targetPlayer = targets.get(CommandConstants.TARGETLIST).get(PLAYERPOSITION);
        Set<Targetable> damageTargetList = game.getCumulativeDamageTargetSet();
        return damageTargetList.contains(targetPlayer);
    }
}
