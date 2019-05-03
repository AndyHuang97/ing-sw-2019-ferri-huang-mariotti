package it.polimi.se2019.server.cards.weapons;

import it.polimi.se2019.server.actions.ActionUnit;
import it.polimi.se2019.server.cards.Card;

import java.util.List;

/**
 * 
 */
public class Weapon extends Card {

    String name;
    private List<ActionUnit> actionUnits;

	/**
	 * Default constructor
	 */

	public Weapon(String name, List<ActionUnit> actionUnitList) {
		super(actionUnitList);
		this.name = name;
	    this.actionUnits = actionUnits;
	}

	public String getName() {
		return name;
	}
}
