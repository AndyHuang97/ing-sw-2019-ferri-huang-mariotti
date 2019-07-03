package it.polimi.se2019.server.playeractions;

import it.polimi.se2019.client.util.Constants;
import it.polimi.se2019.server.cards.Card;
import it.polimi.se2019.server.cards.weapons.Weapon;
import it.polimi.se2019.server.exceptions.UnpackingException;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.player.Player;

import java.util.Arrays;
import java.util.List;

/**
 * This action is used to choose a weapon to shoot with, and then make the Controller switch to WaitingForEffects State.
 * All action units are selected and dealt with in that state, and it expects the actual ShootPlayerAction.
 */
public class ShootWeaponSelection extends PlayerAction {

    private static final String DEFAULT_ERROR_MESSAGE = "Weapon selection action failed:";
    private static final String NOT_LOADED = "is not loaded,";
    private static final String RELOAD_REMAINDER = "please reload first";
    private static final String NOT_IN_BAG = "is not in your weapon bag";

    private static final int WEAPONPOSITIONINPARAMS = 0;

    private Weapon chosenWeapon;

    public ShootWeaponSelection(Game game, Player player) {super(game, player);}
    public ShootWeaponSelection(int amount) {
        super(amount);
    }

    @Override
    public void unpack(List<Targetable> params) throws UnpackingException {
        chosenWeapon = (Weapon) params.get(WEAPONPOSITIONINPARAMS);
    }

    @Override
    public void run() {

    }

    @Override
    public boolean check() {
        // Is weapon loaded?
        if (!chosenWeapon.isLoaded()) {
            setErrorToReport(buildErrorMessage(Arrays.asList(DEFAULT_ERROR_MESSAGE, chosenWeapon.getName(), NOT_LOADED, RELOAD_REMAINDER)));
            return false;
        }
        // player does not have the weapon
        if (!getPlayer().getCharacterState().getWeaponBag().contains(chosenWeapon)) {
            setErrorToReport(buildErrorMessage(Arrays.asList(DEFAULT_ERROR_MESSAGE, chosenWeapon.getName(), NOT_IN_BAG)));
            return false;
        }
        return true;
    }

    @Override
    public Card getCard() {
        return chosenWeapon;
    }

    @Override
    public String getId() {
        return Constants.SHOOT_WEAPON;
    }
}
