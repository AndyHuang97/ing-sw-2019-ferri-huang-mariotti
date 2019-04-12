package it.polimi.SE2019.server.games.board;

/**
 * 
 */
public class Tile {

	private String color;
	private boolean hasSpawn;
	private WeaponCrate weaponCrate;
	private AmmoCrate ammoCrate;
	private LinkType northLink;
	private LinkType southLink;
	private LinkType eastLink;
	private LinkType westLink;

	public Tile(String color, boolean hasSpawn, WeaponCrate weaponCrate, AmmoCrate ammoCrate, LinkType northLink, LinkType southLink, LinkType eastLink, LinkType westLink) {
		this.color = color;
		this.hasSpawn = hasSpawn;
		this.weaponCrate = weaponCrate;
		this.ammoCrate = ammoCrate;
		this.northLink = northLink;
		this.southLink = southLink;
		this.eastLink = eastLink;
		this.westLink = westLink;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public boolean isHasSpawn() {
		return hasSpawn;
	}

	public void setHasSpawn(boolean hasSpawn) {
		this.hasSpawn = hasSpawn;
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
		return northLink;
	}

	public void setNorthLink(LinkType northLink) {
		this.northLink = northLink;
	}

	public LinkType getSouthLink() {
		return southLink;
	}

	public void setSouthLink(LinkType southLink) {
		this.southLink = southLink;
	}

	public LinkType getEastLink() {
		return eastLink;
	}

	public void setEastLink(LinkType eastLink) {
		this.eastLink = eastLink;
	}

	public LinkType getWestLink() {
		return westLink;
	}

	public void setWestLink(LinkType westLink) {
		this.westLink = westLink;
	}
}