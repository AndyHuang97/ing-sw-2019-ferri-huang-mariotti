package it.polimi.se2019.server.actions.effects;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.util.CommandConstants;

import java.util.List;
import java.util.Map;

/**
 * 
 */
public class DamageTarget extends Damage {

	public static final int TARGETPOSITION = 0;

	public DamageTarget(Integer amount) {
		super(amount);
	}

	@Override
	public void run(Game game, Map<String, List<Targetable>> targets) {
		Player target = (Player) targets.get(CommandConstants.TARGET).get(TARGETPOSITION);

		target.getCharacterState().addDamage(game.getCurrentPlayer().getColor(), super.amount);
	}

}