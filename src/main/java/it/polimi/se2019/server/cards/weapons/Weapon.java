package it.polimi.se2019.server.cards.weapons;

import it.polimi.se2019.server.actions.ActionUnit;
import it.polimi.se2019.server.cards.Card;
import it.polimi.se2019.server.games.player.AmmoColor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A weapon is a card that has a few more functionality, in particular if can be loaded, it has a cost tobe reload and to get it.
 * And can have some optional action units.
 *
 * @author FF
 *
 */
public class Weapon extends Card {

	private boolean loaded;
    private List<AmmoColor> pickUpCost;
	private List<AmmoColor> reloadCost;
    private List<ActionUnit> optionalEffectList;

	/**
	 * Default constructor, by default a weapon is loaded
	 *
	 * @param actionUnitList the list of action units
	 * @param name the name
	 * @param pickUpCost the cost to pickup
	 * @param reloadCost the cost to reload
	 * @param optionalEffectList the list of optional effects
	 *
	 */
	public Weapon(List<ActionUnit> actionUnitList, String name, List<AmmoColor> pickUpCost,
				  List<AmmoColor> reloadCost, List<ActionUnit> optionalEffectList) {
		super(actionUnitList, name);
		this.loaded = true;
		this.pickUpCost = pickUpCost;
		this.reloadCost = reloadCost;
		this.optionalEffectList = optionalEffectList;
	}

	/**
	 * The loaded status getter
	 *
	 * @return loaded status
	 *
	 */
	public boolean isLoaded() {
		return loaded;
	}

	/**
	 * Sets the loaded status
	 *
	 * @param loaded new loaded status
	 *
	 */
	public void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}

	/**
	 * The pickup cost getter
	 *
	 * @return pickup cost
	 *
	 */
	public List<AmmoColor> getPickUpCost() {
		return pickUpCost;
	}

	/**
	 * Sets the pickup cost
	 *
	 * @param pickUpCost new pickup cost
	 *
	 */
	public void setPickUpCost(List<AmmoColor> pickUpCost) {
		this.pickUpCost = pickUpCost;
	}

	/**
	 * The reload cost getter
	 *
	 * @return reload cost
	 *
	 */
	public List<AmmoColor> getReloadCost() {
		return reloadCost;
	}

	/**
	 * Sets the reload cost
	 *
	 * @param reloadCost new reload cost
	 *
	 */
	public void setReloadCost(List<AmmoColor> reloadCost) {
		this.reloadCost = reloadCost;
	}

	/**
	 * The optional effects list getter
	 *
	 * @return optional effects
	 *
	 */
	public List<ActionUnit> getOptionalEffectList() {
		return optionalEffectList;
	}

	/**
	 * Sets the optional effects list
	 *
	 * @param optionalEffectList new optional effects list
	 *
	 */
	public void setOptionalEffectList(List<ActionUnit> optionalEffectList) {
		this.optionalEffectList = optionalEffectList;
	}

	/**
	 * The pickup cost getter as a map
	 *
	 * @return pickup cost
	 *
	 */
	public Map<AmmoColor, Integer> getPickupCostAsMap() { return  convert(this.pickUpCost); }

	/**
	 * The reload cost getter as a map
	 *
	 * @return reload cost
	 *
	 */
	public Map<AmmoColor, Integer> getReloadCostAsMap() { return  convert(this.reloadCost); }

	/**
	 * The helper that converts a list to a map
	 *
	 * @param ammo the list of ammo
	 * @return the map of ammo
	 *
	 */
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
