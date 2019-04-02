package it.polimi.SE2019.server.entities;

import it.polimi.SE2019.server.entities.actions.Action;

import java.util.ArrayList;

/**
 * 
 */
public abstract class Entity {

	private ArrayList<Action> action;

	/**
	 * Default constructor
	 */
	public Entity() {
	}

	/**
	 * @return
	 */
	public ArrayList<Action> getAction() {
		// TODO implement here
		return null;
	}

	/**
	 * @param value
	 */
	public void setAction(ArrayList<Action> value) {
		// TODO implement here
	}

}