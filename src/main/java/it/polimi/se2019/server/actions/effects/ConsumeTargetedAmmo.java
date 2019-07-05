package it.polimi.se2019.server.actions.effects;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.player.AmmoColor;
import it.polimi.se2019.util.CommandConstants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This effect consumes only one indicated targeted ammo.
 *
 * @author andreahuang
 *
 */
public class ConsumeTargetedAmmo implements Effect {

    private static final int AMMO_POSITION = 0;

    private Integer amount;

    /**
     * Default constructor. It sets up the amount of targeted ammo to consume.
     *
     * @param amount the amount of targeted ammo.
     */
    public ConsumeTargetedAmmo(Integer amount) {
        this.amount = amount;
    }

    /**
     * This method
     *
     * @param  game the game on which to perform the effect.
     * @param targets the targets is the input of the current player. Either tiles or players.
     */
    @Override
    public void run(Game game, Map<String, List<Targetable>> targets) {
        AmmoColor targetedAmmoColor = (AmmoColor) targets.get(CommandConstants.AMMOCOLOR).get(AMMO_POSITION);
        Map<AmmoColor, Integer> ammoToConsume = new HashMap<>();
        ammoToConsume.put(targetedAmmoColor, amount);
        game.getCurrentPlayer().getCharacterState().consumeAmmo(ammoToConsume, game);

    }
}
