package it.polimi.se2019.server.games.board;

import it.polimi.se2019.server.cards.Card;

import java.util.List;

/**
 * 
 */
public class AmmoCrate {

	private List<Card> powerAmmoList;

	/**
	 * Default constructor
	 * @param powerAmmoList
	 */
	public AmmoCrate(List<Card> powerAmmoList) {
		this.powerAmmoList = powerAmmoList;
	}


	public List<Card> getPowerAmmoList() {
		return powerAmmoList;
	}

	public void setPowerAmmoList(List<Card> powerAmmoList) {
		this.powerAmmoList = powerAmmoList;
	}
}