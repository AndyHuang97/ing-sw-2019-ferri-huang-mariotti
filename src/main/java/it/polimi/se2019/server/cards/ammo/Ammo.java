package it.polimi.se2019.server.cards.ammo;

import it.polimi.se2019.server.cards.Card;

/**
 * 
 */
public class Ammo extends Card {

	private AmmoColor color;

	/**
	 * Default constructor
	 * @param color
	 */
	public Ammo(AmmoColor color) {
		this.color = color;
	}


	public AmmoColor getColor() {
		return color;
	}

	public void setColor(AmmoColor color) {
		this.color = color;
	}
}