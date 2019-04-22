package it.polimi.se2019.server.games.player;

import it.polimi.se2019.server.cards.ammo.Ammo;
import it.polimi.se2019.server.games.PlayerDeath;
import it.polimi.se2019.server.games.board.Tile;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class CharacterState {

	private List<PlayerColor> damageBar;
	private CharacterValue characterValue;
	private List<PlayerColor> markerBar;
	private List<Ammo> ammo;
	private Tile tile;
	private Integer score;

	/**
	 * Default constructor
	 *
	 */

	public CharacterState() {
		this.characterValue = CharacterValue.ZERODEATHS;
		this.damageBar = new ArrayList<>();
		this.markerBar = new ArrayList<>();
		this.ammo = new ArrayList<>();
		this.tile = null;
		this.score = 0;
	}

	/**
	 *  @param damageBar
	 * @param characterValue
	 * @param markerBar
	 * @param ammo
	 * @param tile
	 * @param score
	 */
	public CharacterState(List<PlayerColor> damageBar, CharacterValue characterValue, List<PlayerColor> markerBar, List<Ammo> ammo, Tile tile, Integer score) {
		this.damageBar = damageBar;
		this.characterValue = characterValue;
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

	public void addDamage(PlayerColor playerColor, Integer amount) {
		//TODO need to limit the damgeBar length to 12 as maximum.
		for(int i = 0; i < amount; i++) {
			damageBar.add(playerColor);
		}
	}

	public void resetDamageBar() {
		damageBar.clear();
	}

	public CharacterValue getCharacterValue() {
		return characterValue;
	}

	public void setCharacterValue(CharacterValue characterValue) {
		this.characterValue = characterValue;
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

	public void addMarker(PlayerColor playerColor, Integer amount) {
		//TODO need to add a control so that the number of marker from each player is at most 3.
		for(int i = 0; i < amount; i++) {
			markerBar.add(playerColor);
		}
	}

	public void resetMarkerBar() {
		markerBar.clear();
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

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public void updateScore(PlayerDeath message, PlayerColor playerColor) {

		if(playerColor != message.getDeadPlayer() && message.getAttackers().contains(playerColor)) {
			//TODO will need to modify it when GameMode is implmented (no  first attack bonus in FinalFrenzy).
			if(message.getFirstAttacker() == playerColor) {
				score += 1;
			}

			score += characterValue.getValue(message.getAttackers().indexOf(playerColor));
		}
	}


}