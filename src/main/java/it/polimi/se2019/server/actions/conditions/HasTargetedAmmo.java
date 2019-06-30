package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.player.AmmoColor;
import it.polimi.se2019.util.CommandConstants;

import java.util.List;
import java.util.Map;

public class HasTargetedAmmo implements Condition {

    private static final int AMMO_POSITION = 0;

    private Integer amount;

    public HasTargetedAmmo(Integer amount) {
        this.amount = amount;
    }

    @Override
    public boolean check(Game game, Map<String, List<Targetable>> targets) {
        AmmoColor targetedAmmoColor = (AmmoColor) targets.get(CommandConstants.AMMOCOLOR).get(AMMO_POSITION);
        return game.getCurrentPlayer().getCharacterState().getAmmoBag().get(targetedAmmoColor) >= amount;
    }
}
