package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.player.AmmoColor;
import it.polimi.se2019.util.CommandConstants;

import java.util.List;
import java.util.Map;

/**
 * The HasTargetedAmmo condition class checks the availability of a particular ammo color.
 *
 * @author andreahuang
 */
public class HasTargetedAmmo implements Condition {

    private static final int AMMO_POSITION = 0;

    private Integer amount;

    /**
     * Default contructor. The amount of ammo requested to execute the effect of the card.
     *
     * @param amount the amount of ammo needed
     */
    public HasTargetedAmmo(Integer amount) {
        this.amount = amount;
    }

    /**
     * This condition checks the availability of a particular ammo color.
     *
     * @param game the game on which to perform the evaluation.
     * @param targets the targets is the input of the current player. Either tiles or players.
     * @return true if ammo available, false otherwise.
     */
    @Override
    public boolean check(Game game, Map<String, List<Targetable>> targets) {
        AmmoColor targetedAmmoColor = (AmmoColor) targets.get(CommandConstants.AMMOCOLOR).get(AMMO_POSITION);
        return game.getCurrentPlayer().getCharacterState().getAmmoBag().get(targetedAmmoColor) >= amount;
    }
}
