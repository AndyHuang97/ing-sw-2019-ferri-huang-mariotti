package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;

import java.util.List;
import java.util.Map;

/**
 * 
 */
public interface Condition {
	/**
	 * @return
	 * @param game
	 * @param targets
	 */
	public boolean check(Game game, Map<String, List<Targetable>> targets);

}