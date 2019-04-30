package it.polimi.se2019.server.games.player;

import it.polimi.se2019.server.cards.ammo.AmmoColor;
import it.polimi.se2019.server.games.PlayerDeath;
import it.polimi.se2019.server.games.board.Tile;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

/**
 * 
 */
public class CharacterState {

	private List<PlayerColor> damageBar;
	private CharacterValue characterValue;
	private EnumMap<PlayerColor, Integer> markerBar;
	private EnumMap<AmmoColor, Integer> ammoBag;
	private Tile tile;
	private Integer score;

	/**
	 * Default constructor
	 *
	 */

	public CharacterState() {
		this.characterValue = CharacterValue.ZERODEATHS;
		this.damageBar = new ArrayList<>();
		this.markerBar = new EnumMap<>(PlayerColor.class);
		this.ammoBag = new EnumMap<>(AmmoColor.class);
		this.tile = null;
		this.score = 0;
	}

	/**
	 *  @param damageBar
	 * @param characterValue
	 * @param markerBar
	 * @param ammoBag
	 * @param tile
	 * @param score
	 */
	public CharacterState(List<PlayerColor> damageBar, CharacterValue characterValue,
						  EnumMap<PlayerColor, Integer> markerBar, EnumMap<AmmoColor, Integer> ammoBag,
						  Tile tile, Integer score) {
		this.damageBar = damageBar;
		this.characterValue = characterValue;
		this.markerBar = markerBar;
		this.ammoBag = ammoBag;
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
			if(damageBar.size() < 12) {
				damageBar.add(playerColor);
			}
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
	public EnumMap<PlayerColor, Integer> getMarkerBar() {
		return markerBar;
	}

	/**
	 * @param markerBar
	 */
	public void setMarkerBar(EnumMap<PlayerColor, Integer> markerBar) {
		this.markerBar = markerBar;
	}

	public void addMarker(PlayerColor playerColor, Integer amount) {

		if(markerBar.get(playerColor) + amount > 3) {
			markerBar.put(playerColor, 3);
		}
		else {
			markerBar.put(playerColor, markerBar.get(playerColor) + amount);
		}
	}

	public void resetMarkerBar() {
		markerBar.clear();
	}

	public EnumMap<AmmoColor, Integer> getAmmoBag() {
		return ammoBag;
	}

	public void setAmmoBag(EnumMap<AmmoColor, Integer> ammoBag) {
		this.ammoBag = ammoBag;
	}


	/**
	 * Updates the ammoColor's value in the ammoBag.
	 * @param ammoColor is the ammo type to be updated.
	 * @param amount is a either negative or positive.
	 */
	public void updateAmmoBag(AmmoColor ammoColor, Integer amount) {

		if(ammoBag.get(ammoColor) + amount > 3) {
			ammoBag.put(ammoColor, 3);
		} else {
			ammoBag.put(ammoColor, ammoBag.get(ammoColor) + amount);
		}
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

			score += message.getCharacterValue().getValue(message.getAttackers().indexOf(playerColor));
		}
	}

}