package it.polimi.se2019.server.cards;

import it.polimi.se2019.server.actions.ActionUnit;
import it.polimi.se2019.server.games.Targetable;

import java.util.List;

/**
 * 
 */
public abstract class Card implements Targetable {

	private List<ActionUnit> actionUnitList;
	private String name;
	private String id;

	/**
	 * Default constructor
	 * @param actionUnitList
	 * @param name
	 */
	public Card(List<ActionUnit> actionUnitList, String name) {
		this.actionUnitList = actionUnitList;
		this.name = name;
	}

	public List<ActionUnit> getActionUnitList() {
		return actionUnitList;
	}

	public void setActionUnitList(List<ActionUnit> actionUnitList) {
		this.actionUnitList = actionUnitList;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}