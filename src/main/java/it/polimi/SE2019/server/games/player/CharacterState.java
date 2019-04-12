package it.polimi.SE2019.server.games.player;

import it.polimi.SE2019.server.cards.ammo.Ammo;
import it.polimi.SE2019.server.games.board.Tile;

import java.util.ArrayList;

/**
 * 
 */
public class CharacterState {

	private ArrayList<Color> damageBar;
	private Integer deathCount;
	private ArrayList<Color> markerBar;
	private ArrayList<Ammo> ammo;
	private Tile tile;



	/**
	 * Default constructor
	 * @param damageBar
	 * @param deathCount
	 * @param markerBar
	 * @param ammo
	 * @param tile
	 */
	public CharacterState(ArrayList<Color> damageBar, Integer deathCount, ArrayList<Color> markerBar, ArrayList<Ammo> ammo, Tile tile) {
		this.damageBar = damageBar;
		this.deathCount = deathCount;
		this.markerBar = markerBar;
		this.ammo = ammo;
		this.tile = tile;
	}

	/**
	 * @return damageBar
	 */
	public ArrayList<Color> getDamageBar() {
		return damageBar;
	}

	/**
	 * @param damageBar
	 */
	public void setDamageBar(ArrayList<Color> damageBar) {
		this.damageBar = damageBar;
	}

	/**
	 * @return deathCount
	 */
	public Integer getDeathCount() {
		return deathCount;
	}

	/**
	 * @param deathCount
	 */
	public void setDeathCount(Integer deathCount) {
		this.deathCount = deathCount;
	}

	/**
	 * @return markerBar
	 */
	public ArrayList<Color> getMarkerBar() {
		return markerBar;
	}

	/**
	 * @param markerBar
	 */
	public void setMarkerBar(ArrayList<Color> markerBar) {
		this.markerBar = markerBar;
	}

	/**
	 * @return ammo
	 */
	public ArrayList<Ammo> getAmmo() {
		return ammo;
	}

	/**
	 * @param ammo
	 */
	public void setAmmo(ArrayList<Ammo> ammo) {
		this.ammo = ammo;
	}

	/**
	 * @return tile
	 */
	public Tile getTile() {
		return tile;
	}

	/**
	 *
	 * @param tile
	 */
	public void setTile(Tile tile) {
		this.tile = tile;
	}
}