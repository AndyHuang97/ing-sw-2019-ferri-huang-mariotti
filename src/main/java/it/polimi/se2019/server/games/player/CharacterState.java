package it.polimi.se2019.server.games.player;

import it.polimi.se2019.server.cards.ammo.Ammo;
import it.polimi.se2019.server.games.board.Tile;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class CharacterState {

	private List<PlayerColor> damageBar;
	private Integer deathCount;
	private List<PlayerColor> markerBar;
	private List<Ammo> ammo;
	private Tile tile;
	private Score score;

	/**
	 * Default constructor
	 *
	 */

	public CharacterState() {
		this.damageBar = new ArrayList<>();
		this.deathCount = 0;
		this.markerBar = new ArrayList<>();
		this.ammo = new ArrayList<>();
		this.tile = null;
		this.score = new Score();
	}

	/**
	 *
	 * @param damageBar
	 * @param deathCount
	 * @param markerBar
	 * @param ammo
	 * @param tile
	 */
	public CharacterState(List<PlayerColor> damageBar, Integer deathCount, List<PlayerColor> markerBar, List<Ammo> ammo, Tile tile, Score score) {
		this.damageBar = damageBar;
		this.deathCount = deathCount;
		this.markerBar = markerBar;
		this.ammo = ammo;
		this.tile = tile;
		this.score = score;
	}



	/**
	 * @return damageBar
	 */
	public List<PlayerColor> getDamageBar() {
		return damageBar;
	}

	/**
	 * @param damageBar
	 */
	public void setDamageBar(List<PlayerColor> damageBar) {
		this.damageBar = damageBar;
	}

	public void resetDamageBar() {
		damageBar.clear();
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
	public List<PlayerColor> getMarkerBar() {
		return markerBar;
	}

	/**
	 * @param markerBar
	 */
	public void setMarkerBar(List<PlayerColor> markerBar) {
		this.markerBar = markerBar;
	}

	/**
	 * @return ammo
	 */
	public List<Ammo> getAmmo() {
		return ammo;
	}

	/**
	 * @param ammo
	 */
	public void setAmmo(List<Ammo> ammo) {
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

	public Score getScore() {
		return score;
	}

	public void setScore(Score score) {
		this.score = score;
	}
}