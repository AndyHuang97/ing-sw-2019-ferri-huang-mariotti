package it.polimi.se2019.server.actions.effects;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.util.CommandConstants;

import java.util.List;
import java.util.Map;

/**
 * This effect deals damage to a player target list.
 *
 * @author andreahuang
 *
 */
public class DamageTargetList extends Damage {

    /**
     * Default constructor. It sets up the amount of damage to inflict, and it also has an additional
     * name of action unit for correct targeting.
     *
     * @param amount is the amount of damage to inflict.
     * @param actionUnitName the action unit used for correct targeting.
     */
    public DamageTargetList(Integer amount, String actionUnitName) {
        super(amount, actionUnitName);
    }

    /**
     * This method adds damage to all players in the target list
     *
     * @param  game the game on which to perform the effect.
     * @param targets the targets is the input of the current player. Either tiles or players.
     */
    @Override
    public void run(Game game, Map<String, List<Targetable>> targets) {
        List<Targetable> targetList;
        if (super.actionUnitName == null) {
            targetList = targets.get(CommandConstants.TARGETLIST);
        } else {
            targetList = game.getActionUnitTargetList(super.actionUnitName);
        }

        targetList.forEach(p -> {
                    ((Player) p).getCharacterState().addDamage(game.getCurrentPlayer().getColor(), super.amount, game);
                    game.getCumulativeTargetSet().add(p);
                    game.getCumulativeDamageTargetSet().add(p);
                });

    }
}
