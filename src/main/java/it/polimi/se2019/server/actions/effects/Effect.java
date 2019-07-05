package it.polimi.se2019.server.actions.effects;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;

import java.util.List;
import java.util.Map;

/**
 * The Effect interface gives a common method for all different effects to be used by a card.
 */
public interface Effect {
	/**
	 * This method is run only when all the conditions of the action unit is met.
	 *
	 * @param  game the game on which to perform the effect.
	 * @param targets the targets is the input of the current player. Either tiles or players.
	 */
	public void run(Game game, Map<String, List<Targetable>> targets);
}