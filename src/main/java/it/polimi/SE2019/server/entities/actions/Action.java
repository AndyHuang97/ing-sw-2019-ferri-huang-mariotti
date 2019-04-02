package it.polimi.SE2019.server.entities.actions;

import it.polimi.SE2019.server.Transaction;
import it.polimi.SE2019.server.entities.actions.conditions.Condition;
import it.polimi.SE2019.server.entities.actions.effects.Effect;

import java.util.*;

/**
 * 
 */
public class Action {

	private boolean limited;
	private String name;
	private String description;
	private ArrayList<Effect> effectList;
	private ArrayList<Condition> conditionList;

	/**
	 * Default constructor
	 */
	public Action() {
	}


	/**
	 * @param transaction
	 * @return
	 */
	public boolean check(Transaction transaction) {
		// TODO implement here
		return false;
	}

	/**
	 * @return
	 */
	public void run() {
		// TODO implement here
	}

	/**
	 * @return
	 */
	public boolean getLimited() {
		// TODO implement here
		return false;
	}

	/**
	 * @param value
	 */
	public void setLimited(boolean value) {
		// TODO implement here
	}

	/**
	 * @return
	 */
	public String getName() {
		// TODO implement here
		return "";
	}

	/**
	 * @param value
	 */
	public void setName(String value) {
		// TODO implement here
	}

	/**
	 * @return
	 */
	public String getDescription() {
		// TODO implement here
		return "";
	}

	/**
	 * @param value
	 */
	public void setDescription(String value) {
		// TODO implement here
	}

	/**
	 * @return
	 */
	public ArrayList<Effect> getEffectList() {
		// TODO implement here
		return null;
	}

	/**
	 * @param value
	 */
	public void setEffectList(ArrayList<Effect> value) {
		// TODO implement here
	}

	/**
	 * @return
	 */
	public ArrayList<Condition> getConditionList() {
		// TODO implement here
		return null;
	}

	/**
	 * @param value
	 */
	public void setConditionList(ArrayList<Condition> value) {
		// TODO implement here
	}

}