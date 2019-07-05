package it.polimi.se2019.server.playeractions;

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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * This class represent a ammo grab or weapon grab action. Like all the PlayerAction can be run and checked, his methods
 * should be run by the Controller.
 *
 * @author Andrea Huang
 */
public class GrabPlayerAction extends PlayerAction {

    private static final String DEFAULT_ERROR_MESSAGE = "Grab action failed";
    private static final String NOT_IN = "is not in";
    private static final String[] TILE_SELECT = {"your tile", "the tile you're trying to move"};
    private static final int TILE = 0;
    private static final int VIRTUAL_TILE = 1;
    private static final String ALREADY_HAVE = "you already have";
    private static final String DROP_WEAPON_REMINDER = "select a valid weapon to swap";
    private static final String NO_AMMO = "not enough ammo to pickup";
    private static final String WEAPONS = "weapons";

    private static final int ITEM_POSITION = 0;
    private static final int OPTIONAL_ITEM = 1;
    private static final int MAX_WEAPON_IN_BAG = 3;

    private Weapon weaponToGrab;
    private Weapon weaponToSwap;
    private AmmoCrate ammoToGrab;

    public GrabPlayerAction(Game game, Player player) { super(game, player); }
    public GrabPlayerAction(int amount) { super(amount);}

    /**
     * Unpack the params argument into the object.
     *
     * @requires (* params[0] must have a dynamic type of Weapon or AmmoCrate *) && (* params[1] must have a dynamic
     *           type of Weapon or be omitted *);
     * @param params: list of Targetable objects of dynamic type Tile
     * @throws UnpackingException if the dynamic type of params[0] is not Weapon or AmmoCrate, params[1] exists and is
     *                            not a Weapon (dynamic)
     */
    @Override
    public void unpack(List<Targetable> params) throws UnpackingException {
        boolean weaponCastError = false;
        boolean ammoCastError = false;
        boolean optionalCastError = false;

        try {
            weaponToGrab = (Weapon) params.get(ITEM_POSITION);
        } catch (ClassCastException e) {
            weaponCastError = true;
        }

        try {
            ammoToGrab = (AmmoCrate) params.get(ITEM_POSITION);
        } catch (ClassCastException e) {
            ammoCastError = true;
        }

        try {
             weaponToSwap = (Weapon) params.get(OPTIONAL_ITEM);
        } catch (IndexOutOfBoundsException | ClassCastException e) {
            optionalCastError = true;
        }

        if (weaponCastError && ammoCastError && optionalCastError) {
            throw new UnpackingException();
        }
    }

    @Override
    public boolean check() {
        // access the player position that will be set during run phase
        Tile playerPosition = getGame().getVirtualPlayerPosition();
        // tileSelect is used to select the right string TILE_SELECT and generate a correct error message
        int tileSelect = TILE;

        if (playerPosition == null) {
            playerPosition = getPlayer().getCharacterState().getTile();
            tileSelect = VIRTUAL_TILE;
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

            if (weaponBagSize == MAX_WEAPON_IN_BAG && weaponToSwap != null) {
                boolean isWeaponToSwapInWeaponBag = false;

                for (Weapon weapon : getPlayer().getCharacterState().getWeaponBag()) {
                    if (weapon.equals(weaponToSwap)) {
                        isWeaponToSwapInWeaponBag = true;
                    }
                }

                if (!isWeaponToSwapInWeaponBag) {
                    setErrorToReport(buildErrorMessage(Arrays.asList(DEFAULT_ERROR_MESSAGE, ALREADY_HAVE, ((Integer) MAX_WEAPON_IN_BAG).toString(), WEAPONS, DROP_WEAPON_REMINDER)));
                    return false;
                }

            } else if (weaponBagSize >= MAX_WEAPON_IN_BAG) {
                setErrorToReport(buildErrorMessage(Arrays.asList(DEFAULT_ERROR_MESSAGE, ALREADY_HAVE, ((Integer) MAX_WEAPON_IN_BAG).toString(), WEAPONS, DROP_WEAPON_REMINDER)));
                return false;
            }

            // pickup cost
            Map<AmmoColor, Integer> pickupCost = weaponToGrab.getPickupCostAsMap();
            Map<AmmoColor, Integer> availableAmmo = getPlayer().getCharacterState().getAmmoBag();

            for (Map.Entry<AmmoColor, Integer> cost : pickupCost.entrySet()) {
                try {
                    if (cost.getValue() > availableAmmo.get(cost.getKey())) {
                        Logger.getGlobal().info("Not enough ammo!");
                        setErrorToReport(buildErrorMessage(Arrays.asList(DEFAULT_ERROR_MESSAGE, NO_AMMO, weaponToGrab.getId())));
                        return false;
                    }
                } catch (NullPointerException e) {
                    return false;
                }
            }

            setErrorToReport(buildErrorMessage(Arrays.asList(DEFAULT_ERROR_MESSAGE, weaponToGrab.getId(), NOT_IN, TILE_SELECT[tileSelect])));

            return isWeaponAvailable;
        }

        else if (ammoToGrab != null) {
            AmmoCrate ammoCrate = playerPosition.getAmmoCrate();

            // check if player is asking to grab ammo in his tile,
            // furthermore should check the conditions of the ActionUnits in ammoToGrab but
            // there are no condition in AmmoCrates
            if (ammoCrate != null) {
                return (ammoCrate.getId().equals(ammoToGrab.getId()));
            } else {
                return false;
            }
        }

        return false;
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
            player.getCharacterState().addWeapon(weaponToGrab);

            // pay pickup cost
            player.getCharacterState().consumeAmmo(weaponToGrab.getPickupCostAsMap(), getGame());

            // consume the weapon crate card
            List<Weapon> weaponCrate = playerTile.getWeaponCrate();
            weaponCrate.remove(weaponToGrab);

            if (weaponToSwap != null) {
                getPlayer().getCharacterState().getWeaponBag().remove(weaponToSwap);
                weaponCrate.add(weaponToSwap);
            }

            getGame().getBoard().setWeaponCrate(x, y, weaponCrate);

            //getGame().getBoard().setWeaponCrate(x, y, weaponCrate);
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
