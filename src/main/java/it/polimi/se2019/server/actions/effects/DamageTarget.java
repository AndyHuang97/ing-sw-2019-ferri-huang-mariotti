package it.polimi.se2019.server.actions.effects;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.games.player.PlayerColor;

import java.util.Map;

/**
 * 
 */
public class DamageTarget implements Effect {

	private Player targetPlayer;
	private PlayerColor attackerColor;
    private Integer amount;

	/**
	 * Default constructor
	 */
	public DamageTarget(Player targetPlayer, PlayerColor attackerColor, Integer amount) {
		this.targetPlayer = targetPlayer;
		this.attackerColor = attackerColor;
		this.amount = amount;
	}

	@Override
	public void run(Game game, Map<String, Map<Targetable, Integer>> targets) {
		targetPlayer.getCharacterState().addDamage(attackerColor, amount);
	}

}