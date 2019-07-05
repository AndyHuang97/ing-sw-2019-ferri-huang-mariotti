package it.polimi.se2019.server.actions.effects;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.player.AmmoColor;

import java.util.List;
import java.util.Map;

/**
 * This effect replaces ammo but its not used
 *
 * @author FF
 *
 */
public class ReplaceAmmo implements Effect {

    private AmmoColor ammoColor;

    /**
     * Default construtctor.
     *
     * @param ammoColor color of the ammo to replace
     *
     */
    public ReplaceAmmo(AmmoColor ammoColor) {
        this.ammoColor = ammoColor;
    }

    /**
     * Replaces some specified ammo, but its not implemented because its not used
     *
     * @param game the game
     * @param targets the target to swap
     */
    @Override
    public void run(Game game, Map<String, List<Targetable>> targets) {

    }
}
