package it.polimi.se2019.server.cards;

import it.polimi.se2019.server.actions.ActionUnit;
import it.polimi.se2019.server.games.Targetable;

import java.util.List;

/**
 * 
 */
public abstract class Card implements Targetable {

	private List<ActionUnit> actionUnitList;

	/**
	 * Default constructor
	 * @param actionUnitList
	 */
	public Card(List<ActionUnit> actionUnitList) {
		this.actionUnitList = actionUnitList;
	}

	public List<ActionUnit> getActionUnitList() {
		return actionUnitList;
	}

	public void setActionUnitList(List<ActionUnit> actionUnitList) {
		this.actionUnitList = actionUnitList;
	}
}