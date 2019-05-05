package it.polimi.se2019.server.actions.effects;

import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.games.player.PlayerColor;

/**
 * 
 */
public class MarkTarget implements Effect {

	private Player targetPlayer;
	private PlayerColor attackerColor;
	private Integer amount;

	/**
	 * Default constructor
	 * @param targetPlayer
	 * @param attackerColor
	 * @param amount
	 */
	public MarkTarget(Player targetPlayer, PlayerColor attackerColor, Integer amount) {
		this.targetPlayer = targetPlayer;
		this.attackerColor = attackerColor;
		this.amount = amount;
	}

	@Override
	public void run() {
		targetPlayer.getCharacterState().addMarker(attackerColor, amount);
	}
}