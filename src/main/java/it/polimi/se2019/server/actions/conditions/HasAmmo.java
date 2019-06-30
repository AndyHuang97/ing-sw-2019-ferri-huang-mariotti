package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.player.AmmoColor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * This condition checks whether the attacker has enough ammo.
 */
public class HasAmmo implements Condition {

    private Map<AmmoColor, Integer> ammoNeeded;

    public HasAmmo(Map<AmmoColor, Integer> ammoNeeded) {
        this.ammoNeeded = ammoNeeded;
    }

    public  Map<AmmoColor, Integer> getAmmoNeeded() {
        return ammoNeeded;
    }

    @Override
    public boolean check(Game game, Map<String, List<Targetable>> targets) {
        Map<AmmoColor, Integer> ammoBag = game.getCurrentPlayer().getCharacterState().getAmmoBag();

        return Arrays.stream(AmmoColor.values())
                .allMatch(color -> {
                    boolean result = true;
                    int powerUpAmount = game.getCurrentPlayer().getCharacterState().powerUpCount(color);
                    int remainingAmmo = ammoBag.get(color) + powerUpAmount - ammoNeeded.get(color);

                    System.out.println("powerUp amount: " + powerUpAmount);
                    System.out.println("remainingAmmo " + remainingAmmo);

                    if(remainingAmmo < 0) {
                        result = false;
                    }
                    Logger.getGlobal().warning("HasAmmo: " + result);
                    return result;
                });
    }
}