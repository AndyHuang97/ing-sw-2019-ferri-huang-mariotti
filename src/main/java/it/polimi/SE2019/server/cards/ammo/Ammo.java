package it.polimi.SE2019.server.cards.ammo;

import it.polimi.SE2019.server.cards.Card;

/**
 * 
 */
public class Ammo extends Card {

	private String color;

	/**
	 * Default constructor
	 * @param color
	 */
	public Ammo(String color) {
		this.color = color;
	}


	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
}