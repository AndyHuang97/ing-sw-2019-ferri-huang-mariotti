package it.polimi.se2019.server.cards;

import it.polimi.se2019.server.actions.ActionUnit;
import it.polimi.se2019.server.games.Targetable;

import java.util.List;

/**
 * A card is the abstract form of weapons, powerups and ammo crates
 *
 * @author FF
 *
 */
public abstract class Card implements Targetable {

	private List<ActionUnit> actionUnitList;
	private String name;

	/**
	 * Default constructor
	 * @param actionUnitList
	 * @param name
	 *
	 */
	public Card(List<ActionUnit> actionUnitList, String name) {
		this.actionUnitList = actionUnitList;
		this.name = name;
	}

	/**
	 * The effects list getter
	 *
	 * @return effects
	 *
	 */
	public List<ActionUnit> getActionUnitList() {
		return actionUnitList;
	}

	/**
	 * Sets the effects list
	 *
	 * @param actionUnitList new effects list
	 *
	 */
	public void setActionUnitList(List<ActionUnit> actionUnitList) {
		this.actionUnitList = actionUnitList;
	}

	/**
	 * The name getter, the name of the card its also its id
	 *
	 * @return the name
	 *
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name
	 *
	 * @param name the new name
	 *
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * The id getter, the name of the card its also its id
	 *
	 * @return the id
	 *
	 */
	@Override
	public String getId() {
		return name;
	}

}