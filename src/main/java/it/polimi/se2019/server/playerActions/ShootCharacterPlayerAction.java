package it.polimi.se2019.server.playerActions;

import it.polimi.se2019.server.actions.ActionUnit;
import it.polimi.se2019.server.cards.weapons.Weapon;
import it.polimi.se2019.server.exceptions.UnpackingException;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.util.ConditionConstants;
import it.polimi.se2019.util.ErrorResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShootCharacterPlayerAction extends PlayerAction {

    private final int targetPositionInParams = 0;
    private final int weaponPositionInParams = 1;
    private final int actionUnitPositionInParams = 2;

    private final String errorMessage = "Shoot action failed";
    private Player target;
    private Weapon chosenWeapon;
    private ActionUnit chosenActionUnit;

    public ShootCharacterPlayerAction(Game game, Player player) { super(game, player); }

    @Override
    public void unpack(List<Targetable> params) throws UnpackingException {
        target = (Player) params.get(targetPositionInParams);
        chosenWeapon = (Weapon) params.get(weaponPositionInParams);
        chosenActionUnit = (ActionUnit) params.get(actionUnitPositionInParams);
    }

    @Override
    public void run() {


    }

    @Override
    public boolean check() {
        /**
         * Check that target is in Player view
         * Check that Player is using a valid weapon (and has ammo)
         *  - check that target position matches the weapon requirement
         */

        // Is weapon loaded?
        if (!chosenWeapon.isLoaded()) {
            return false;
        }

        // Is chosenActionUnit in chosenWeapon?
        if (chosenWeapon.getActionUnitList().stream().noneMatch(availableActionUnit -> availableActionUnit == chosenActionUnit)) {
            return false;
        }

        // build Map<String, List<Targetable>>
        Map<String, List<Targetable>> conditionParams = new HashMap<>();
        List<Targetable> targetableList = new ArrayList<>();

        targetableList.add(target);
        conditionParams.put(ConditionConstants.TARGET, targetableList);

        return chosenActionUnit.check(getGame(), conditionParams);
    }

    @Override
    public ErrorResponse getErrorMessage() {
        return null;
    }
}
