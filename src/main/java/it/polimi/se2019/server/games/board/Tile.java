package it.polimi.se2019.server.games.board;

/**
 * 
 */
public class Tile {

	private String color;
	private boolean spawnTile;
	private WeaponCrate weaponCrate;
	private AmmoCrate ammoCrate;
	private LinkType[] links;

	/**
	 *
	 * @param color
	 * @param spawnTile
	 * @param weaponCrate
	 * @param ammoCrate
	 * @param links is an array with 4 cells: 0 - north, 1 - south, 2 - east, 3 - west.
	 */

	public Tile(String color, boolean spawnTile, WeaponCrate weaponCrate, AmmoCrate ammoCrate, LinkType[] links) {
		this.color = color;
		this.spawnTile = spawnTile;
		this.weaponCrate = weaponCrate;
		this.ammoCrate = ammoCrate;
		this.links = links;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public boolean isSpawnTile() {
		return spawnTile;
	}

	public void setHasSpawn(boolean spawnTile) {
		this.spawnTile = spawnTile;
	}

	public WeaponCrate getWeaponCrate() {
		return weaponCrate;
	}

	public void setWeaponCrate(WeaponCrate weaponCrate) {
		this.weaponCrate = weaponCrate;
	}

	public AmmoCrate getAmmoCrate() {
		return ammoCrate;
	}

	public void setAmmoCrate(AmmoCrate ammoCrate) {
		this.ammoCrate = ammoCrate;
	}

	public LinkType getNorthLink() {
		return links[0];
	}

	public void setNorthLink(LinkType northLink) {
		links[0] = northLink;
	}

	public LinkType getSouthLink() {
		return links[1];
	}

	public void setSouthLink(LinkType southLink) {
		links[1] = southLink;
	}

	public LinkType getEastLink() {
		return links[2];
	}

	public void setEastLink(LinkType eastLink) {
		links[2] = eastLink;
	}

	public LinkType getWestLink() {
		return links[3];
	}

	public void setWestLink(LinkType westLink) {
		links[3] = westLink;
	}
}