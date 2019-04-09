package it.polimi.SE2019.server.cards.weapons;

import it.polimi.SE2019.server.actions.ActionUnit;
import it.polimi.SE2019.server.cards.Card;

import java.util.ArrayList;

/**
 * 
 */
public class Weapon extends Card {

    String name;
    private ArrayList<ActionUnit> actions;

	/**
	 * Default constructor
	 */
	public Weapon(String name, ArrayList<ActionUnit> actions) {
	    this.name = name;
	    this.actions = actions;
	}


}