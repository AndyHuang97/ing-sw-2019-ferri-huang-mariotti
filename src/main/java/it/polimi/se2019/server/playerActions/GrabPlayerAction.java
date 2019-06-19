package it.polimi.se2019.server.playerActions;

import it.polimi.se2019.server.cards.weapons.Weapon;
import it.polimi.se2019.server.exceptions.UnpackingException;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.SpawnTile;
import it.polimi.se2019.server.games.player.AmmoColor;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.util.ErrorResponse;

import java.util.List;
import java.util.Map;

public class GrabPlayerAction extends PlayerAction {

    private static final int WEAPONPOSITION = 0;
    private static final int MAXWEAPONINBAG = 3;
    private static final String errorMessage = "Grab action failed";

    private Weapon weaponToGrab;

    public GrabPlayerAction(Game game, Player player) { super(game, player); }

    @Override
    public void unpack(List<Targetable> params) throws UnpackingException {
        try {
            weaponToGrab = (Weapon) params.get(WEAPONPOSITION);
        } catch (ClassCastException e) {
            throw new UnpackingException();
        }
    }

    /**
     * Checks if the Weapon the player is trying to grab is in the weapon crate of the Tile the player is in.
     */
    @Override
    public boolean check() {
        try {
            // assert that the player is on a SpawnTile
            SpawnTile playerPosition = (SpawnTile) getPlayer().getCharacterState().getTile();

            List<Weapon> weaponCrate = playerPosition.getWeaponCrate();

            // assert that the weapon is avabile in the SpawnTile
            boolean isWeaponAvailable = false;

            for (Weapon weapon : weaponCrate) {
                if(weapon == weaponToGrab) {
                    isWeaponAvailable = true;
                }
            }

            // assert that player have 2 or less weapon
            if (getPlayer().getCharacterState().getWeaponBag().size() >= MAXWEAPONINBAG) {
                return false;
            }

            // pickup cost
            Map<AmmoColor, Integer> pickupCost = weaponToGrab.getPickupCostAsMap();
            Map<AmmoColor, Integer> availableAmmo = getPlayer().getCharacterState().getAmmoBag();

            for (Map.Entry<AmmoColor, Integer> cost : pickupCost.entrySet()) {
                try {
                    if (cost.getValue() > availableAmmo.get(cost.getKey())) {
                        return false;
                    }
                } catch (NullPointerException e) {
                    return false;
                }
            }

            return isWeaponAvailable;

        } catch (ClassCastException e) {
            return false;
        }
    }

    @Override
    public ErrorResponse getErrorMessage() {
        return new ErrorResponse(errorMessage);
    }

    @Override
    public void run() {
        Player player = getGame().getCurrentPlayer();

        player.getCharacterState().addWeapon(weaponToGrab);

        // pay pickup cost
        player.getCharacterState().consumeAmmo(weaponToGrab.getPickupCostAsMap());
    }
}
