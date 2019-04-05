package it.polimi.SE2019.server.games.board;

/**
 * 
 */
public class Tile {

	private String color;
	private boolean hasSpawn;
	private WeaponCrate weapon;
	private AmmoCrate ammo;
	private int position;

	/**
	 * Default constructor
	 */
	public Tile() {
	}


	/**
	 * @return
	 */
	public WeaponCrate getAvailableWeapons() {
		// TODO implement here
		return null;
	}

	/**
	 * @return
	 */
	public boolean isWeaponAvailable() {
		// TODO implement here
		return false;
	}

	/**
	 * @return
	 */
	public String getColor() {
		// TODO implement here
		return "";
	}

	/**
	 * @param value
	 */
	public void setColor(String value) {
		// TODO implement here
	}

	/**
	 * @return
	 */
	public void getHasSpawn() {
		// TODO implement here
	}

	/**
	 * @param value
	 */
	public void setHasSpawn(boolean value) {
		// TODO implement here
	}

	/**
	 * @return
	 */
	public WeaponCrate getWeapon() {
		// TODO implement here
		return null;
	}

	/**
	 * @param value
	 */
	public void setWeapon(WeaponCrate value) {
		// TODO implement here
	}

	/**
	 * @return
	 */
	public AmmoCrate getAmmo() {
		// TODO implement here
		return null;
	}

	/**
	 * @param value
	 */
	public void setAmmo(AmmoCrate value) {
		// TODO implement here
	}

	/**
	 * @return
	 */
	public int getPosition() {
		// TODO implement here
		return 0;
	}

	/**
	 * @param value
	 */
	public void setPosition(int value) {
		// TODO implement here
	}

}