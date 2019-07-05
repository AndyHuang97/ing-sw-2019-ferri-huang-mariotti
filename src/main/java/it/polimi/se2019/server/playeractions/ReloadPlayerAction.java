package it.polimi.se2019.server.playeractions;

import it.polimi.se2019.client.util.Constants;
import it.polimi.se2019.server.cards.Card;
import it.polimi.se2019.server.cards.weapons.Weapon;
import it.polimi.se2019.server.exceptions.UnpackingException;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.player.AmmoColor;
import it.polimi.se2019.server.games.player.Player;

import java.util.*;

public class ReloadPlayerAction extends PlayerAction {
    private static final String ERROR_MESSAGE = "Reload action failed:";
    private static final String NOT_ENOUGH = "not enough";
    private static final String AMMO = "ammo";

    private List<Weapon> weaponToReload = new ArrayList<>();

    public ReloadPlayerAction(Game game, Player player) { super(game, player); }
    public ReloadPlayerAction(int amount) { super(amount);}

    @Override
    public void unpack(List<Targetable> params) throws UnpackingException {
        try {
            for (Targetable weapon : params) {
                weaponToReload.add((Weapon) weapon);
            }
        } catch (ClassCastException e) {
            throw new UnpackingException();
        }
    }

    @Override
    public void run() {
        for (Weapon weapon : weaponToReload) {
            Map<AmmoColor, Integer> reloadCost = weapon.getReloadCostAsMap();

            getPlayer().getCharacterState().consumeAmmo(reloadCost, getGame());

            getGame().setLoaded(weapon, true);
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
                    setErrorToReport(buildErrorMessage(Arrays.asList(ERROR_MESSAGE, NOT_ENOUGH, ammoColor.getKey().getColor(), AMMO)));
                    return false;
                }

            } catch (NullPointerException e) {
                return false;
            }
        }

        return true;
    }

    @Override
    public Card getCard() {
        return null;
    }

    @Override
    public String getId() {
        return Constants.RELOAD;
    }
}
