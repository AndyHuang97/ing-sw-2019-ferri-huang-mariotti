package it.polimi.se2019.server.actions.effects;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;

import java.util.List;
import java.util.Map;

/**
 * 
 */
public interface Effect {
	/**
	 * @return
	 * @param game
	 * @param targets
	 */
	public void run(Game game, Map<String, List<Targetable>> targets);
}