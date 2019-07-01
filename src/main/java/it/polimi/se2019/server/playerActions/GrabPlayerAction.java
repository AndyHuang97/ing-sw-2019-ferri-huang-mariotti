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
import java.util.logging.Logger;

public class GrabPlayerAction extends PlayerAction {

    private static final int ITEMPOSITION = 0;
    private static final int OPTIONALITEM = 1;
    private static final int MAXWEAPONINBAG = 3;
    private static final String ERRORMESSAGE = "Grab action failed";

    private Weapon weaponToGrab;
    private Weapon weaponToDiscard;
    private AmmoCrate ammoToGrab;

    public GrabPlayerAction(Game game, Player player) { super(game, player); }
    public GrabPlayerAction(int amount) { super(amount);}

    @Override
    public void unpack(List<Targetable> params) throws UnpackingException {
        boolean weaponCastError = false;
        boolean ammoCastError = false;
        boolean optionalCastError = false;

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

        try {
             weaponToDiscard = (Weapon) params.get(OPTIONALITEM);
        } catch (IndexOutOfBoundsException | ClassCastException e) {
            optionalCastError = true;
        }

        if (weaponCastError && ammoCastError && optionalCastError) {
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

        Logger.getGlobal().info(playerPosition.getId());
        if (weaponToGrab != null) {
            List<Weapon> weaponCrate = playerPosition.getWeaponCrate();

            // assert that the weapon is available in the SpawnTile
            boolean isWeaponAvailable = false;
            try {
                Logger.getGlobal().info("Weapon to grab " + weaponToGrab.getId());
                for (Weapon weapon : weaponCrate) {
                    Logger.getGlobal().info("Weapon in bag " + weapon.getId());
                    if (weapon.equals(weaponToGrab)) {
                        Logger.getGlobal().info("Weapon is available: " + weapon.getId());
                        isWeaponAvailable = true;
                    }
                }
            } catch (NullPointerException e) {
                return false;
            }

            // assert that player have 2 or less weapons
            int weaponBagSize = getPlayer().getCharacterState().getWeaponBag().size();

            if (weaponBagSize == 3 && weaponToDiscard != null) {
                boolean isWeaponToDiscardInWeaponBag = false;

                for (Weapon weapon : getPlayer().getCharacterState().getWeaponBag()) {
                    if (weapon.equals(weaponToDiscard)) {
                        isWeaponToDiscardInWeaponBag = true;
                    }
                }

                if (!isWeaponToDiscardInWeaponBag) return false;

            } else if (weaponBagSize >= MAXWEAPONINBAG) {
                return false;
            }

            // pickup cost
            Map<AmmoColor, Integer> pickupCost = weaponToGrab.getPickupCostAsMap();
            Map<AmmoColor, Integer> availableAmmo = getPlayer().getCharacterState().getAmmoBag();

            for (Map.Entry<AmmoColor, Integer> cost : pickupCost.entrySet()) {
                try {
                    if (cost.getValue() > availableAmmo.get(cost.getKey())) {
                        Logger.getGlobal().info("Not enough ammo!");
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
            return (ammoCrate.equals(ammoToGrab));
        }

        return false;
    }

    @Override
    public ErrorResponse getErrorMessage() {
        return new ErrorResponse(ERRORMESSAGE);
    }

    @Override
    public Card getCard() {
        if (weaponToGrab != null) {
            return weaponToGrab;
        }
        else {
            return ammoToGrab;
        }
    }

    @Override
    public void run() {
        Player player = getGame().getCurrentPlayer();
        Tile playerTile = player.getCharacterState().getTile();

        int x = playerTile.getxPosition();
        int y = playerTile.getyPosition();

        if (weaponToGrab != null) {
            if (weaponToDiscard != null) {
                getPlayer().getCharacterState().getWeaponBag().remove(weaponToDiscard);
                getGame().discardWeapon(weaponToDiscard);
            }

            player.getCharacterState().addWeapon(weaponToGrab);

            // pay pickup cost
            player.getCharacterState().consumeAmmo(weaponToGrab.getPickupCostAsMap(), getGame());

            // consume the ammoCrate card
            List<Weapon> weaponCrate = playerTile.getWeaponCrate();
            weaponCrate.remove(weaponToGrab);

            getGame().getBoard().setWeaponCrate(x, y, weaponCrate);
        }

        else if (ammoToGrab != null) {
            for (ActionUnit actionUnit : ammoToGrab.getActionUnitList()) {
                actionUnit.run(getGame(), null);
            }

            getGame().getBoard().setAmmoCrate(x, y, null);
        }
    }

    @Override
    public String getId() {
        return Constants.GRAB;
    }
}
