package it.polimi.SE2019.server.games.board;

import it.polimi.SE2019.server.cards.weapons.Weapon;

import java.util.*;

/**
 * 
 */
public class WeaponCrate {

	private ArrayList<Weapon> weaponList;

	/**
	 * Default constructor
	 * @param weaponList
	 */
	public WeaponCrate(ArrayList<Weapon> weaponList) {
		this.weaponList = weaponList;
	}


	public ArrayList<Weapon> getWeaponList() {
		return weaponList;
	}

	public void setWeaponList(ArrayList<Weapon> weaponList) {
		this.weaponList = weaponList;
	}
}