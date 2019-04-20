package it.polimi.SE2019.server.cards.weapons;

import it.polimi.SE2019.server.actions.ActionUnit;
import it.polimi.SE2019.server.cards.Card;

import java.util.ArrayList;

/**
 * 
 */
public class Weapon extends Card {

    String name;
    private ArrayList<ActionUnit> actionUnits;

	/**
	 * Default constructor
	 */

	public Weapon(String name, ArrayList<ActionUnit> actionUnits) {
	    this.name = name;
	    this.actionUnits = actionUnits;
	}


}
