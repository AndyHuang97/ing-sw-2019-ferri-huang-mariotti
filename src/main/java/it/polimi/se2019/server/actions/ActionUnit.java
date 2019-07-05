package it.polimi.se2019.server.actions;

import it.polimi.se2019.server.actions.conditions.Condition;
import it.polimi.se2019.server.actions.effects.Effect;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;

import java.util.List;
import java.util.Map;

/**
 * The action unit is the basic action used in the game, it has effects and conditions to be triggered. Other variable stored here are used to help
 * build the clients.
 *
 * @author FF
 *
 */
public class ActionUnit implements Targetable {

	private boolean available;
	private String name;
	private String description;
	private transient List<Effect> effectList;
	private transient List<Condition> conditionList;
	private transient Map<String, List<Targetable>> commands;
	private boolean unidirectional;
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
	public ActionUnit(boolean available, String name, String description, List<Effect> effectList, List<Condition> conditionList, boolean unidirectional, int numPlayerTargets, int numTileTargets, boolean playerSelectionFirst) {
		this.available = available;
		this.name = name;
		this.description = description;
		this.effectList = effectList;
		this.conditionList = conditionList;
		this.unidirectional = unidirectional;
		this.numPlayerTargets = numPlayerTargets;
		this.numTileTargets = numTileTargets;
		this.playerSelectionFirst = playerSelectionFirst;
	}

	/**
	 * Each action unit has a check that checks all the conditions in the action unit and makes sure all are respected
	 *
	 * @param game the game
	 * @param targets the targetables
	 * @return a boolean response
	 *
	 */
	public boolean check(Game game, Map<String, List<Targetable>> targets) {
		for (Condition c : conditionList) {
			try {
				boolean result = c.check(game, targets);

				if (!result) {
					return false;
				}
			} catch (IndexOutOfBoundsException e) {
				return false;
			}
        }
        return true;
	}

	/**
	 * Each action unit has a run method that execute the action aster the check is successful
	 *
	 * @param game the game
	 * @param commands the targetables
	 *
	 */
	public void run(Game game, Map<String, List<Targetable>> commands) {
		for (Effect e : effectList) {
		    e.run(game, commands);
        }
		this.setCommands(commands);
		game.getCurrentActionUnitsList().add(this);
	}

	/**
	 * The getter for availability
	 *
	 * @return yes or no
	 *
	 */
	public boolean isAvailable() {
		return available;
	}

	/**
	 * The setter for availability
	 *
	 * @param available new status
	 *
	 */
	public void setAvailable(boolean available) {
		this.available = available;
	}

	/**
	 * The getter for the name
	 *
	 * @return name
	 *
	 */
	public String getName() {
		return name;
	}

	/**
	 * The setter for the name
	 *
	 * @param name new name
	 *
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * The getter for the description
	 *
	 * @return description
	 *
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * The setter for the description
	 *
	 * @param description new description
	 *
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * The getter for the effect list
	 *
	 * @return effects
	 *
	 */
	public List<Effect> getEffectList() {
		return effectList;
	}

	/**
	 * The setter for the effect list
	 *
	 * @param effectList new effect list
	 *
	 */
	public void setEffectList(List<Effect> effectList) {
		this.effectList = effectList;
	}

	/**
	 * The getter for the conditions list
	 *
	 * @return conditions
	 *
	 */
	public List<Condition> getConditionList() {
		return conditionList;
	}

	/**
	 * The setter for the conditions list
	 *
	 * @param conditionList new conditions list
	 *
	 */
	public void setConditionList(List<Condition> conditionList) {
		this.conditionList = conditionList;
	}

	/**
	 * The getter for the unidirectional condition
	 *
	 * @return is unidirectional
	 *
	 */
	public boolean isUnidirectional() {
		return unidirectional;
	}

	/**
	 * The setter for the unidirectional condition
	 *
	 * @param unidirectional new unidirectional condition
	 *
	 */
	public void setUnidirectional(boolean unidirectional) {
		this.unidirectional = unidirectional;
	}

	/**
	 * The getter for the maximum number of targets that are players
	 *
	 * @return number of players
	 *
	 */
	public int getNumPlayerTargets() {
		return numPlayerTargets;
	}

	/**
	 * The setter for the maximum number of targets that are players
	 *
	 * @param numPlayerTargets new number of targets that are players
	 *
	 */
	public void setNumPlayerTargets(int numPlayerTargets) {
		this.numPlayerTargets = numPlayerTargets;
	}

	/**
	 * The getter for the maximum number of targets that are tiles
	 *
	 * @return number of tiles
	 *
	 */
	public int getNumTileTargets() {
		return numTileTargets;
	}

	/**
	 * The setter for the maximum number of targets that are tiles
	 *
	 * @param numTileTargets new number of targets that are tiles
	 *
	 */
	public void setNumTileTargets(int numTileTargets) {
		this.numTileTargets = numTileTargets;
	}

	/**
	 * The getter to notify that you have to select players first
	 *
	 * @return players first
	 *
	 */
	public boolean isPlayerSelectionFirst() {
		return playerSelectionFirst;
	}

	/**
	 * The setter to notify that you have to select players first
	 *
	 * @param playerSelectionFirst new select players first
	 *
	 */
	public void setPlayerSelectionFirst(boolean playerSelectionFirst) {
		this.playerSelectionFirst = playerSelectionFirst;
	}

	/**
	 * The getter for the commands
	 *
	 * @return commands
	 *
	 */
	public Map<String, List<Targetable>> getCommands() {
		return commands;
	}

	/**
	 * The setter for the commands
	 *
	 * @param commands the commands
	 *
	 */
	public void setCommands(Map<String, List<Targetable>> commands) {
		this.commands = commands;
	}

	/**
	 * The getter for the id
	 *
	 * @return the name is the id
	 *
	 */
	@Override
	public String getId() {
		return name;
	}


}