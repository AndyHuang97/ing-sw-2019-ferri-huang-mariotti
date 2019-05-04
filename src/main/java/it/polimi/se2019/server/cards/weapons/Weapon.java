package it.polimi.se2019.server.cards.weapons;

import it.polimi.se2019.server.actions.ActionUnit;
import it.polimi.se2019.server.cards.Card;

import java.util.List;


public class Weapon extends Card {

    String name;
    private List<ActionUnit> actionUnits;

	public Weapon(String name, List<ActionUnit> actionUnitList) {
		super(actionUnitList);
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
