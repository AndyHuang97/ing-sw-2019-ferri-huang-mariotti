package it.polimi.se2019.server.actions;

import it.polimi.se2019.server.actions.conditions.Condition;
import it.polimi.se2019.server.actions.effects.Effect;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;

import java.util.List;
import java.util.Map;

/**
 * 
 */
public class ActionUnit implements Targetable {

	private boolean available;
	private String name;
	private String description;
	private List<Effect> effectList;
	private List<Condition> conditionList;
	private int numPlayerTargets;
	private int numTileTargets;
	private boolean playerSelectionFirst;

	/**
	 * Default constructor
	 * @param available
	 * @param name
	 * @param effectList
	 * @param conditionList
	 * @param numPlayerTargets
	 * @param numTileTargets
	 * @param playerSelectionFirst
	 */
	public ActionUnit(boolean available, String name, List<Effect> effectList, List<Condition> conditionList, int numPlayerTargets, int numTileTargets, boolean playerSelectionFirst) {
		/*
		TODO: remove and available or justify them!
		 */
		this.available = available;
		this.name = name;
		//this.description = description;
		this.effectList = effectList;
		this.conditionList = conditionList;
		this.numPlayerTargets = numPlayerTargets;
		this.numTileTargets = numTileTargets;
		this.playerSelectionFirst = playerSelectionFirst;
	}

	public boolean check(Game game, Map<String, List<Targetable>> targets) {
		return false;
	}

	public void run() {

	}

	public void getRequiredInputs() {

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

	public int getNumPlayerTargets() {
		return numPlayerTargets;
	}

	public void setNumPlayerTargets(int numPlayerTargets) {
		this.numPlayerTargets = numPlayerTargets;
	}

	public int getNumTileTargets() {
		return numTileTargets;
	}

	public void setNumTileTargets(int numTileTargets) {
		this.numTileTargets = numTileTargets;
	}

	public boolean isPlayerSelectionFirst() {
		return playerSelectionFirst;
	}

	public void setPlayerSelectionFirst(boolean playerSelectionFirst) {
		this.playerSelectionFirst = playerSelectionFirst;
	}
}