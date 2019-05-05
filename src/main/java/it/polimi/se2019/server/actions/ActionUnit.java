package it.polimi.se2019.server.actions;

import it.polimi.se2019.server.Transaction;
import it.polimi.se2019.server.actions.conditions.Condition;
import it.polimi.se2019.server.actions.effects.Effect;

import java.util.*;

/**
 * 
 */
public class ActionUnit {

	private boolean available;
	private String name;
	private String description;
	private List<Effect> effectList;
	private List<Condition> conditionList;

	/**
	 * Default constructor
	 * @param available
	 * @param name
	 * @param effectList
	 * @param conditionList
	 */
	public ActionUnit(boolean available, String name, List<Effect> effectList, List<Condition> conditionList) {
		/*
		TODO: remove and available or justify them!
		 */
		this.available = available;
		this.name = name;
		//this.description = description;
		this.effectList = effectList;
		this.conditionList = conditionList;
	}

	public boolean check(Transaction transaction) {
		return false;
	}

	public void run() {

	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
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

	public List<Effect> getEffectList() {
		return effectList;
	}

	public void setEffectList(List<Effect> effectList) {
		this.effectList = effectList;
	}

	public List<Condition> getConditionList() {
		return conditionList;
	}

	public void setConditionList(List<Condition> conditionList) {
		this.conditionList = conditionList;
	}
}