package it.polimi.se2019.server.playerActions;

import it.polimi.se2019.server.actions.ActionUnit;
import it.polimi.se2019.server.cards.weapons.Weapon;
import it.polimi.se2019.server.exceptions.UnpackingException;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.player.Player;

import java.util.List;

public class ShootPlayerAction extends PlayerAction {

    private final String errorMessage = "Shoot action failed";
    private Player target;
    private Weapon choosenWeapon;
    private ActionUnit choosenActionUnit;

    public ShootPlayerAction(Game game, Player player) { super(game, player); }

    @Override
    public void unpack(List<Targetable> params) throws UnpackingException {

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
        List<Player> visibleTargets = getGame().getCurrentPlayer().getCharacterState().getTile().getVisibleTargets(getGame());

        /*
        // Is target visible?
        if (!visibleTargets.stream().anyMatch(visibleTarget-> visibleTarget == target)) {
            return false;
        }
        */

        // Is weapon loaded?
        if (!choosenWeapon.isLoaded()) {
            return false;
        }

        // Is choosenActionUnit in choosenWeapon?
        if (!choosenWeapon.getActionUnitList().stream().anyMatch(availableActionUnit -> availableActionUnit == choosenActionUnit)) {
            return false;
        }

        // Generare Map<String, List<Targetable>>

        // if (!choosenActionUnit.check())

        return true;
    }

    @Override
    public String getErrorMessage() {
        return null;
    }
}
