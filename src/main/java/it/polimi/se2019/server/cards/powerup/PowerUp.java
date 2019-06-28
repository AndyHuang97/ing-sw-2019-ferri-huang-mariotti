package it.polimi.se2019.server.cards.powerup;

import it.polimi.se2019.server.actions.ActionUnit;
import it.polimi.se2019.server.cards.Card;
import it.polimi.se2019.server.games.board.RoomColor;
import it.polimi.se2019.server.games.player.AmmoColor;

import java.util.List;

/**
 * 
 */
public class PowerUp extends Card {

	private AmmoColor color;

	/**
	 * Default constructor
	 */
	public PowerUp(List<ActionUnit> actionUnitList, String name, AmmoColor color) {
        super(actionUnitList, name);
        this.color = color;
    }

	public AmmoColor getPowerUpColor() {
		return color;
	}

	public void setColor(AmmoColor color) {
		this.color = color;
	}
}