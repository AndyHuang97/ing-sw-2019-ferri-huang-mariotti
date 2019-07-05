package it.polimi.se2019.server.cards.powerup;

import it.polimi.se2019.server.actions.ActionUnit;
import it.polimi.se2019.server.cards.Card;
import it.polimi.se2019.server.games.board.RoomColor;
import it.polimi.se2019.server.games.player.AmmoColor;

import java.util.List;

/**
 * A powerup has the ammocolor as an attribute plus all it works as a card
 *
 * @author FF
 *
 */
public class PowerUp extends Card {

	private AmmoColor color;

	/**
	 * Default constructor
	 *
	 * @param actionUnitList the list of action units
	 * @param name the name
	 * @param color the color
	 *
	 */
	public PowerUp(List<ActionUnit> actionUnitList, String name, AmmoColor color) {
        super(actionUnitList, name);
        this.color = color;
    }

	/**
	 * The color getter
	 *
	 * @return the color
	 *
	 */
	public AmmoColor getPowerUpColor() {
		return color;
	}

	/**
	 * Sets the color
	 *
	 * @param color the color
	 *
	 */
	public void setColor(AmmoColor color) {
		this.color = color;
	}
}