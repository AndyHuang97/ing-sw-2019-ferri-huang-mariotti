package it.polimi.se2019.server.playerActions;

import it.polimi.se2019.server.cards.weapons.Weapon;
import it.polimi.se2019.server.exceptions.UnpackingException;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.player.AmmoColor;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.util.ErrorResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReloadPlayerAction extends PlayerAction {

    private static final int WEAPONLISTPOSITION = 0;

    private List<Weapon> weaponToReload;

    public ReloadPlayerAction(Game game, Player player) { super(game, player); }

    @Override
    public void unpack(List<Targetable> params) throws UnpackingException {
        try {
            weaponToReload = (List<Weapon>) params.get(WEAPONLISTPOSITION);
        } catch (ClassCastException e) {
            throw new UnpackingException();
        }
    }

    @Override
    public void run() {
        for (Weapon weapon : weaponToReload) {
            Map<AmmoColor, Integer> reloadCost = convert(weapon.getReloadCost());

            getPlayer().getCharacterState().consumeAmmo(reloadCost);

            weapon.setLoaded(true);
        }
    }

    @Override
    public boolean check() {

        Map<AmmoColor, Integer> actualAmmo = getPlayer().getCharacterState().getAmmoBag();

        Map<AmmoColor, Integer> neededAmmo = new HashMap<>();

        for (Weapon weapon : weaponToReload) {
            List<AmmoColor> reloadCost = weapon.getReloadCost();

            for (AmmoColor ammoColor : reloadCost) {
                // initialize to zero if absent
                neededAmmo.putIfAbsent(ammoColor, 0);

                // +1
                neededAmmo.put(ammoColor, neededAmmo.get(ammoColor) + 1);
            }
        }

        for (Map.Entry<AmmoColor, Integer> ammoColor : neededAmmo.entrySet()) {
            Integer neededAmount = ammoColor.getValue();

            try {
                Integer availableAmount = actualAmmo.get(ammoColor.getKey());

                if (availableAmount < neededAmount) {
                    return false;
                }

            } catch (NullPointerException e) {
                return false;
            }
        }

        return true;
    }

    @Override
    public ErrorResponse getErrorMessage() {
        return null;
    }

    private Map<AmmoColor, Integer> convert(List<AmmoColor> ammo) {

        Map<AmmoColor, Integer> convertedAmmo = new HashMap<>();

        for (AmmoColor ammoColor : ammo) {
            // initialize to zero if absent
            convertedAmmo.putIfAbsent(ammoColor, 0);

            // +1
            convertedAmmo.put(ammoColor, convertedAmmo.get(ammoColor) + 1);
        }

        return convertedAmmo;
    }
}
