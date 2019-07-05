package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;

import java.util.List;
import java.util.Map;

/**
 * The Condition interface gives a common method for all different conditions to be met by a card before activating
 * its effect.
 *
 * @author andreahuang
 */
public interface Condition {
	/**
	 * The check method receives the game and evaluates the targets depending on the condition which is deserialized
	 * directly from the json file of the cards.
	 *
	 * @return a boolean, result of the condition evaluation.
	 * @param game the game on which to perform the evaluation.
	 * @param targets the targets is the input of the current player. Either tiles or players.
	 */
	boolean check(Game game, Map<String, List<Targetable>> targets);

}