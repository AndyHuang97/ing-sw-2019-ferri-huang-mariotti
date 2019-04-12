package it.polimi.SE2019.server.games.board;

import it.polimi.SE2019.server.cards.Card;

import java.util.ArrayList;

/**
 * 
 */
public class AmmoCrate {

	private ArrayList<Card> powerAmmoList;

	/**
	 * Default constructor
	 * @param powerAmmoList
	 */
	public AmmoCrate(ArrayList<Card> powerAmmoList) {
		this.powerAmmoList = powerAmmoList;
	}


	public ArrayList<Card> getPowerAmmoList() {
		return powerAmmoList;
	}

	public void setPowerAmmoList(ArrayList<Card> powerAmmoList) {
		this.powerAmmoList = powerAmmoList;
	}
}