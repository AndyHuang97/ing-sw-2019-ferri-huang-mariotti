package it.polimi.se2019.server.games.player;

import it.polimi.se2019.server.cards.powerup.PowerUp;
import it.polimi.se2019.server.cards.weapons.Weapon;
import it.polimi.se2019.server.games.PlayerDeath;
import it.polimi.se2019.server.games.board.Tile;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

/**
 * 
 */
public class CharacterState {

	private CharacterValue characterValue;
	private List<PlayerColor> damageBar;
	private EnumMap<PlayerColor, Integer> markerBar;
	private EnumMap<AmmoColor, Integer> ammoBag;
	private List<Weapon> weapoonBag;
	private List<PowerUp> powerUpBag;
	private Tile tile;
	private Integer score;

	/**
	 * Default constructor
	 *
	 */

	public CharacterState() {
		this.characterValue = CharacterValue.ZERODEATHS;
		this.damageBar = new ArrayList<>();
		this.markerBar = initMarkerBar();
		this.ammoBag = initAmmoBag();
		this.weapoonBag = new ArrayList<>();
		this.powerUpBag = new ArrayList<>();
		this.tile = null;
		this.score = 0;
	}

	/**
	 * @param damageBar
	 * @param characterValue
	 * @param markerBar
	 * @param ammoBag
	 * @param weapoonBag
	 * @param powerUpBag
	 * @param tile
	 * @param score
	 */
	public CharacterState(List<PlayerColor> damageBar, CharacterValue characterValue,
						  EnumMap<PlayerColor, Integer> markerBar, EnumMap<AmmoColor, Integer> ammoBag,
						  List<Weapon> weapoonBag, List<PowerUp> powerUpBag, Tile tile, Integer score) {
		this.damageBar = damageBar;
		this.characterValue = characterValue;
		this.markerBar = markerBar;
		this.ammoBag = ammoBag;
		this.weapoonBag = weapoonBag;
		this.powerUpBag = powerUpBag;
		this.tile = tile;
		this.score = score;
	}


	public CharacterValue getCharacterValue() {
		return characterValue;
	}

	public void setCharacterValue(CharacterValue characterValue) {
		this.characterValue = characterValue;
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

	public EnumMap<PlayerColor, Integer> initMarkerBar() {
		EnumMap<PlayerColor, Integer> markerBar = new EnumMap<>(PlayerColor.class);
		markerBar.put(PlayerColor.BLUE, 0);
		markerBar.put(PlayerColor.GREEN, 0);
		markerBar.put(PlayerColor.GREY, 0);
		markerBar.put(PlayerColor.PURPLE, 0);
		markerBar.put(PlayerColor.YELLOW, 0);

		return markerBar;
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

		if (!markerBar.containsKey(playerColor)){
			markerBar.put(playerColor, amount);
		}
		else {
			if (markerBar.get(playerColor) + amount > 3) {
				markerBar.put(playerColor, 3);
			} else {
				markerBar.put(playerColor, markerBar.get(playerColor) + amount);
			}
		}
	}

	public void resetMarkerBar() {
		markerBar.clear();
	}

	/**
	 * This method initializes the ammoBag by creating a new instance and setting the values of all keys to 0.
	 * @return the newly created ammoBag.
	 */
	public EnumMap<AmmoColor, Integer> initAmmoBag() {
		EnumMap<AmmoColor, Integer> ammoBag = new EnumMap<>(AmmoColor.class);
		ammoBag.put(AmmoColor.BLUE, 0);
		ammoBag.put(AmmoColor.RED, 0);
		ammoBag.put(AmmoColor.YELLOW, 0);

		return ammoBag;
	}

	/**
	 * This method returns the player's ammoBag.
	 * @return player's ammoBag.
	 */
	public EnumMap<AmmoColor, Integer> getAmmoBag() {
		return ammoBag;
	}

	/**
	 * This method sets a new reference for the ammoBag.
	 * @param ammoBag is the new ammoBag.
	 */
	public void setAmmoBag(EnumMap<AmmoColor, Integer> ammoBag) {
		this.ammoBag = ammoBag;
	}

	/**
	 * The addAmmo method adds a certain amount of new ammo to the ammoBag;
	 * it keeps an ammo color's max value to 3.
	 * @param ammoToAdd is a map containing the amount of each ammo color to add to the player's ammoBag.
	 */
	public void addAmmo(EnumMap<AmmoColor, Integer> ammoToAdd) {
		ammoToAdd.keySet().stream()
				.forEach(k -> {
					if (ammoBag.get(k) + ammoToAdd.get(k) > 3) {
						ammoBag.put(k, 3);
					} else {
						ammoBag.put(k, ammoBag.get(k) + ammoToAdd.get(k));
					}
				});
	}

	/**
	 * The consumeAmmo method consumes a certain amount of ammo from the ammoBag;
	 * it keeps an ammo color's max value to 0.
	 * @param ammoToConsume is a map containing the amount of each ammo color to consume from the player's ammoBag.
	 */
	public void consumeAmmo(EnumMap<AmmoColor, Integer> ammoToConsume) {
		ammoToConsume.keySet().stream()
				.forEach(k -> ammoBag.put(k, ammoBag.get(k) - ammoToConsume.get(k)));
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

	public List<Weapon> getWeapoonBag() {
		return weapoonBag;
	}

	public void addWeapon(Weapon weapon) {
		weapoonBag.add(weapon);
	}

	public void setWeapoonBag(List<Weapon> weapoonBag) {
		this.weapoonBag = weapoonBag;
	}

	public List<PowerUp> getPowerUpBag() {
		return powerUpBag;
	}

	public void addPowerUp(PowerUp powerUp) {
		powerUpBag.add(powerUp);
	}

	public void setPowerUpBag(List<PowerUp> powerUpBag) {
		this.powerUpBag = powerUpBag;
	}
}