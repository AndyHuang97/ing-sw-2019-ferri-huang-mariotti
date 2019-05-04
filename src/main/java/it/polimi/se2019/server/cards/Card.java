package it.polimi.se2019.server.cards;

import it.polimi.se2019.server.actions.ActionUnit;

import java.util.List;

/**
 * 
 */
public abstract class Card {

	private List<ActionUnit> actionUnitList;

	/**
	 * Default constructor
	 * @param actionUnitList
	 */
	public Card(List<ActionUnit> actionUnitList) {
		this.actionUnitList = actionUnitList;
	}

}