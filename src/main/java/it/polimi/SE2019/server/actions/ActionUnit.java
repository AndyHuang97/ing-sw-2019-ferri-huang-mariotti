package it.polimi.SE2019.server.actions;

import it.polimi.SE2019.server.Transaction;
import it.polimi.SE2019.server.actions.conditions.Condition;
import it.polimi.SE2019.server.actions.effects.Effect;

import java.util.*;

/**
 * 
 */
public class ActionUnit {

	private boolean limited;
	private String name;
	private String description;
	private ArrayList<Effect> effectList;
	private ArrayList<Condition> conditionList;

	/**
	 * Default constructor
	 * @param limited
	 * @param name
	 * @param description
	 * @param effectList
	 * @param conditionList
	 */
	public ActionUnit(boolean limited, String name, String description, ArrayList<Effect> effectList, ArrayList<Condition> conditionList) {
		this.limited = limited;
		this.name = name;
		this.description = description;
		this.effectList = effectList;
		this.conditionList = conditionList;
	}

	public boolean check(Transaction transaction) {
		return false;
	}

	public void run() {

	}

	public boolean isLimited() {
		return limited;
	}

	public void setLimited(boolean limited) {
		this.limited = limited;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ArrayList<Effect> getEffectList() {
		return effectList;
	}

	public void setEffectList(ArrayList<Effect> effectList) {
		this.effectList = effectList;
	}

	public ArrayList<Condition> getConditionList() {
		return conditionList;
	}

	public void setConditionList(ArrayList<Condition> conditionList) {
		this.conditionList = conditionList;
	}
}