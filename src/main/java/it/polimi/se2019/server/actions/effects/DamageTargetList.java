package it.polimi.se2019.server.actions.effects;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.util.CommandConstants;

import java.util.List;
import java.util.Map;

public class DamageTargetList extends Damage {

    public DamageTargetList(Integer amount, String actionUnitName) {
        super(amount, actionUnitName);
    }

    @Override
    public void run(Game game, Map<String, List<Targetable>> targets) {
        List<Targetable> targetList;
        if (super.actionUnitName == null) {
            targetList = targets.get(CommandConstants.TARGETLIST);
        } else {
            targetList = game.getActionUnitTargetList(super.actionUnitName);
        }

        targetList.stream()
                .forEach(p -> {
                    ((Player) p).getCharacterState().addDamage(game.getCurrentPlayer().getColor(), super.amount, game);
                    game.getCumulativeTargetSet().add(p);
                    game.getCumulativeDamageTargetSet().add(p);
                });

    }
}
