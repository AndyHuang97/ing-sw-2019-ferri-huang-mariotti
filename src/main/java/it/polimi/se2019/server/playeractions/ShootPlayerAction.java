package it.polimi.se2019.server.playeractions;

import it.polimi.se2019.client.util.Constants;
import it.polimi.se2019.server.actions.ActionUnit;
import it.polimi.se2019.server.cards.Card;
import it.polimi.se2019.server.cards.weapons.Weapon;
import it.polimi.se2019.server.exceptions.UnpackingException;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.player.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class ShootPlayerAction extends PlayerAction {

    private static final String DEFAULT_ERROR_MESSAGE = "Shoot action failed:";
    private static final String NOT_LOADED = "is not loaded,";
    private static final String RELOAD_REMAINDER = "please reload first";
    private static final String NO_WEAPON_SELECTED = "no weapon selected";
    private static final String NO_MODE_SELECTED = "no fire mode selected";

    private Weapon chosenWeapon;
    private ActionUnit chosenActionUnit;
    private Map<String, List<Targetable>> inputCommands;

    public ShootPlayerAction(Game game, Player player) { super(game, player); }
    public ShootPlayerAction(int amount) { super(amount);}

    @Override
    public void unpack(List<Targetable> params) throws UnpackingException {

        inputCommands = buildCommandDict(params);

        chosenWeapon = (Weapon) params.stream()
                .filter(t -> getPlayer().getCharacterState().getWeaponBag().contains(t))
                .findAny().orElse(null);

        if (chosenWeapon != null) {
            chosenActionUnit = (ActionUnit) params.stream()
                    .filter(t -> chosenWeapon.getActionUnitList().contains(t) || chosenWeapon.getOptionalEffectList().contains(t))
                    .findAny().orElse(null);
        }
    }

    @Override
    public void run() {
        // since ActionUnit.run() signature changed we need to build a Map<String, List<Targetable>>
        // in order to run the action
        chosenActionUnit.run(getGame(), inputCommands);
    }

    @Override
    public boolean check() {
        // Is weapon loaded?
        if (chosenWeapon == null) {
            setErrorToReport(buildErrorMessage(Arrays.asList(DEFAULT_ERROR_MESSAGE, NO_WEAPON_SELECTED)));
            return false;
        }
        else if (chosenActionUnit == null) {
            setErrorToReport(buildErrorMessage(Arrays.asList(DEFAULT_ERROR_MESSAGE, NO_MODE_SELECTED)));
            return false;
        }
        Logger.getGlobal().info("Valid weapon and action unit");
        if (!chosenWeapon.isLoaded()) {
            setErrorToReport(buildErrorMessage(Arrays.asList(DEFAULT_ERROR_MESSAGE, chosenWeapon.getName(), NOT_LOADED, RELOAD_REMAINDER)));
            return false;
        }

        return chosenActionUnit.check(getGame(), inputCommands);
    }

    public Weapon getChosenWeapon() {
        return chosenWeapon;
    }

    @Override
    public Card getCard() {
        return chosenWeapon;
    }

    @Override
    public String getId() {
        return Constants.SHOOT;
    }
}
