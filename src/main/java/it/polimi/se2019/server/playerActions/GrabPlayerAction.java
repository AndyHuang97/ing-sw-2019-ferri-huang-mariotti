package it.polimi.se2019.server.playerActions;

import it.polimi.se2019.client.util.Constants;
import it.polimi.se2019.server.actions.ActionUnit;
import it.polimi.se2019.server.cards.Card;
import it.polimi.se2019.server.cards.ammocrate.AmmoCrate;
import it.polimi.se2019.server.cards.weapons.Weapon;
import it.polimi.se2019.server.exceptions.UnpackingException;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.AmmoColor;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.util.ErrorResponse;

import java.util.List;
import java.util.Map;

public class GrabPlayerAction extends PlayerAction {

    private static final int ITEMPOSITION = 0;
    private static final int MAXWEAPONINBAG = 3;
    private static final String ERRORMESSAGE = "Grab action failed";

    private Weapon weaponToGrab;
    private AmmoCrate ammoToGrab;

    public GrabPlayerAction(Game game, Player player) { super(game, player); }
    public GrabPlayerAction(int amount) { super(amount);}

    @Override
    public void unpack(List<Targetable> params) throws UnpackingException {
        boolean weaponCastError = false;
        boolean ammoCastError = false;

        try {
            weaponToGrab = (Weapon) params.get(ITEMPOSITION);
        } catch (ClassCastException e) {
            weaponCastError = true;
        }

        try {
            ammoToGrab = (AmmoCrate) params.get(ITEMPOSITION);
        } catch (ClassCastException e) {
            ammoCastError = true;
        }

        if (weaponCastError && ammoCastError) {
            throw new UnpackingException();
        }
    }

    /**
     * Checks if the Weapon the player is trying to grab is in the weapon crate of the Tile the player is in.
     */
    @Override
    public boolean check() {
        // access the player position that will be set during run phase
        Tile playerPosition = getGame().getVirtualPlayerPosition();

        if (playerPosition == null) {
            playerPosition = getPlayer().getCharacterState().getTile();
        }

        if (weaponToGrab != null) {
            List<Weapon> weaponCrate = playerPosition.getWeaponCrate();

            // assert that the weapon is available in the SpawnTile
            boolean isWeaponAvailable = false;
            try {
                for (Weapon weapon : weaponCrate) {
                    if (weapon == weaponToGrab) {
                        isWeaponAvailable = true;
                    }
                }
            } catch (NullPointerException e) {
                return false;
            }

            // assert that player have 2 or less weapons
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
        }

        else if (ammoToGrab != null) {
            AmmoCrate ammoCrate = playerPosition.getAmmoCrate();

            // check if player is asking to grab ammo in his tile,
            // furthermore should check the conditions of the ActionUnits in ammoToGrab but
            // there are no condition in AmmoCrates
            return (ammoCrate == ammoToGrab);
        }

        return false;
    }

    @Override
    public ErrorResponse getErrorMessage() {
        return new ErrorResponse(ERRORMESSAGE);
    }

    @Override
    public Card getCard() {
        return weaponToGrab;
    }

    @Override
    public void run() {
        if (weaponToGrab != null) {
            Player player = getGame().getCurrentPlayer();

            player.getCharacterState().addWeapon(weaponToGrab);

            // pay pickup cost
            player.getCharacterState().consumeAmmo(weaponToGrab.getPickupCostAsMap());
        }

        else if (ammoToGrab != null) {
            for (ActionUnit actionUnit : ammoToGrab.getActionUnitList()) {
                actionUnit.run(getGame(), null);
            }
        }
    }

    @Override
    public String getId() {
        return Constants.GRAB;
    }
}
