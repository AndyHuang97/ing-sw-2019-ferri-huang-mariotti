package it.polimi.SE2019.server.games.board;

/**
 * 
 */
public class Tile {

	private String color;
	private boolean hasSpawn;
	private WeaponCrate weaponCrate;
	private AmmoCrate ammoCrate;
	private int position;

	/**
	 * Default constructor
	 * @param color
	 * @param hasSpawn
	 * @param weaponCrate
	 * @param ammoCrate
	 * @param position
	 */
	public Tile(String color, boolean hasSpawn, WeaponCrate weaponCrate, AmmoCrate ammoCrate, int position) {
		this.color = color;
		this.hasSpawn = hasSpawn;
		this.weaponCrate = weaponCrate;
		this.ammoCrate = getAmmoCrate();
		this.position = position;
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
	public WeaponCrate getWeaponCrate() {
		// TODO implement here
		return null;
	}

	/**
	 * @param value
	 */
	public void setWeaponCrate(WeaponCrate value) {
		// TODO implement here
	}

	/**
	 * @return
	 */
	public AmmoCrate getAmmoCrate() {
		// TODO implement here
		return null;
	}

	/**
	 * @param value
	 */
	public void setAmmoCrate(AmmoCrate value) {
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