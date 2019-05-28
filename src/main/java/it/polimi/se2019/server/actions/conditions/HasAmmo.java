package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.player.AmmoColor;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * This condition checks whether the attacker has enough ammo.
 */
public class HasAmmo implements Condition {

    private Map<AmmoColor, Integer> ammoNeeded;

    public HasAmmo(Map<AmmoColor, Integer> ammoNeeded) {
        this.ammoNeeded = ammoNeeded;
    }

    @Override
    public boolean check(Game game, Map<String, List<Targetable>> targets) {
        Map<AmmoColor, Integer> ammoBag = game.getCurrentPlayer().getCharacterState().getAmmoBag();

        return Arrays.stream(AmmoColor.values())
                .allMatch(color -> {
                    boolean result = true;
                    if(ammoBag.get(color) - ammoNeeded.get(color) < 0) {
                        result = false;
                    }
                    return result;
                });

    }
}