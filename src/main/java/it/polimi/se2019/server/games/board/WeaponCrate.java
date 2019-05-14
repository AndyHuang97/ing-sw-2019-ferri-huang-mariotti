package it.polimi.se2019.server.games.board;

import it.polimi.se2019.server.cards.weapons.Weapon;

import java.util.*;

@Deprecated
public class WeaponCrate {

	private List<Weapon> weaponList;

	/**
	 * Default constructor
	 * @param weaponList
	 */
	public WeaponCrate(List<Weapon> weaponList) {
		this.weaponList = weaponList;
	}


	public List<Weapon> getWeaponList() {
		return weaponList;
	}

	public void setWeaponList(List<Weapon> weaponList) {
		this.weaponList = weaponList;
	}
}