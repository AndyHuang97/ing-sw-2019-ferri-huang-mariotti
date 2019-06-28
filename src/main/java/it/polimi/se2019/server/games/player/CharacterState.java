package it.polimi.se2019.server.games.player;

import it.polimi.se2019.server.cards.powerup.PowerUp;
import it.polimi.se2019.server.cards.weapons.Weapon;
import it.polimi.se2019.server.dataupdate.CharacterStateUpdate;
import it.polimi.se2019.server.dataupdate.PlayerEventListenable;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.PlayerDeath;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.playerActions.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class contains the information about a character, it's meant to be serialized.
 * A read-only copy of this object should be stored in the client (view).
 */
public class CharacterState extends PlayerEventListenable implements Serializable {

	public static final int[] NORMAL_VALUE_BAR = {8,6,4,2,1,1};
	public static final int[] FRENZY_VALUE_BAR = {2,1,1,1};
	private static final int FIRST_ATTACKER = 0;

	private int deaths;
	private int[] valueBar;
	private List<PlayerColor> damageBar;
	private Map<PlayerColor, Integer> markerBar;
	private Map<AmmoColor, Integer> ammoBag;
	private List<Weapon> weaponBag;
	private List<PowerUp> powerUpBag;
	private Tile tile;
	private Integer score;
	private boolean firstSpawn;
	private boolean connected;

	private boolean beforeFrenzyActivator;

	/**
	 * Default constructor
	 *
	 */

	public CharacterState() {
		this.deaths = 0;
		this.valueBar = NORMAL_VALUE_BAR;
		this.damageBar = new ArrayList<>();
		this.markerBar = initMarkerBar();
		this.ammoBag = initAmmoBag();
		this.weaponBag = new ArrayList<>();
		this.powerUpBag = new ArrayList<>();
		this.tile = null;
		this.score = 0;
		this.firstSpawn = true;
		this.connected = true;
	}

	/**
	 * @param damageBar
	 * @param markerBar
	 * @param ammoBag
	 * @param weaponBag
	 * @param powerUpBag
	 * @param tile
	 * @param score
	 */
	public CharacterState(int deaths, int[] valueBar, List<PlayerColor> damageBar, Map<PlayerColor, Integer> markerBar,
						  Map<AmmoColor, Integer> ammoBag, List<Weapon> weaponBag,
						  List<PowerUp> powerUpBag, Tile tile, Integer score, Boolean connected) {
        this.deaths = deaths;
        this.valueBar = valueBar;
	    this.damageBar = damageBar;
		this.markerBar = markerBar;
		this.ammoBag = ammoBag;
		this.weaponBag = weaponBag;
		this.powerUpBag = powerUpBag;
		this.tile = tile;
		this.score = score;
		this.connected = connected;
	}

	/**
	 * The getPossibleActions method returns the list of actions that a player can perform according to their damage bar
	 * and the mode of the game(normal or frenzy).
	 * @param isFrenzy is a boolean that indicates the game mode.
	 * @return the list of allowed actions.
	 */
	//TODO could be deserialized (?)
	public List<CompositeAction> getPossibleActions(boolean isFrenzy) {
		List<CompositeAction> possibleActions = new ArrayList<>();
		possibleActions.add(new CompositeAction(PlayerAction.NOP));
		possibleActions.add(new CompositeAction(PlayerAction.GRAB));
		possibleActions.add(new CompositeAction(PlayerAction.SHOOT));
		if (isFrenzy) {
			possibleActions.add(new CompositeAction(PlayerAction.RELOAD, PlayerAction.SHOOT));
			if (beforeFrenzyActivator) {
				possibleActions.add(new CompositeAction(new MovePlayerAction(1), PlayerAction.RELOAD,
						PlayerAction.SHOOT));
				possibleActions.add(new CompositeAction(new MovePlayerAction(4)));
				possibleActions.add(new CompositeAction(new MovePlayerAction(2), PlayerAction.GRAB));
			} else {
				possibleActions.add(new CompositeAction(new MovePlayerAction(2), PlayerAction.RELOAD,
						PlayerAction.SHOOT));
				possibleActions.add(new CompositeAction(new MovePlayerAction(3), PlayerAction.GRAB));
			}
		} else {
			possibleActions.add(new CompositeAction(new MovePlayerAction(3)));
			possibleActions.add(new CompositeAction(new MovePlayerAction(1), PlayerAction.GRAB));
			possibleActions.add(new CompositeAction(PlayerAction.RELOAD));
			if (this.getDamageBar().size()>=3) {
				possibleActions.add(new CompositeAction(new MovePlayerAction(2), PlayerAction.GRAB));
				if (this.getDamageBar().size()>=6) {
					possibleActions.add(new CompositeAction(new MovePlayerAction(1), PlayerAction.SHOOT));
				}
			}
		}

		return possibleActions;
	}


	/**
	 * The swapValueBar method swaps out the current value bar of the player with the correct one according to
	 * the game mode.
	 * @param isFrenzy is a boolean that indicates the game mode.
	 */
	public void swapValueBar(boolean isFrenzy) {
		if (isFrenzy) {
			if (this.getDamageBar().isEmpty()) {
				valueBar = FRENZY_VALUE_BAR;
			} else {
				valueBar = NORMAL_VALUE_BAR;
			}
		}
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

	public void addDamage(PlayerColor playerColor, Integer amount, Game game) {
	    amount += getMarker(playerColor);
	    resetMarkerBar(playerColor);

		// finds the owner of this character state and then adds it to the list of damaged players
		Player player = game.getPlayerList().stream().filter(p -> p.getCharacterState().equals(this)).findFirst().orElse(null);
		game.getCumulativeDamageTargetSet().add(player);

		for(int i = 0; i < amount; i++) {
			if(damageBar.size() < 12) {
				damageBar.add(playerColor);
			}
		}
	}

	public void resetDamageBar() {
		damageBar.clear();
	}

	public Map<PlayerColor, Integer> initMarkerBar() {
		Map<PlayerColor, Integer> markerBar = new HashMap<>();
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
	public Map<PlayerColor, Integer> getMarkerBar() {
		return markerBar;
	}

	/**
	 * @param markerBar
	 */
	public void setMarkerBar(Map<PlayerColor, Integer> markerBar) {
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

	public void resetMarkerBar(PlayerColor playerColor) {
	    getMarkerBar().put(playerColor, 0);
    }

	public int getMarker(PlayerColor playerColor) {
	    return getMarkerBar().get(playerColor);
    }

	/**
	 * Resets all key's values to 0.
	 *
	 */
	public void resetMarkerBar() {
		markerBar.keySet()
				.forEach(k -> markerBar.put(k, 0));
	}

	/**
	 * This method initializes the ammoBag by creating a new instance and setting the values of all keys to 0.
	 * @return the newly created ammoBag.
	 */
	public Map<AmmoColor, Integer> initAmmoBag() {
		Map<AmmoColor, Integer> ammoBag = new HashMap<>();
		ammoBag.put(AmmoColor.BLUE, 0);
		ammoBag.put(AmmoColor.RED, 0);
		ammoBag.put(AmmoColor.YELLOW, 0);

		return ammoBag;
	}

	/**
	 * This method returns the player's ammoBag.
	 * @return player's ammoBag.
	 */
	public Map<AmmoColor, Integer> getAmmoBag() {
		return ammoBag;
	}

	/**
	 * This method sets a new reference for the ammoBag.
	 * @param ammoBag is the new ammoBag.
	 */
	public void setAmmoBag(Map<AmmoColor, Integer> ammoBag) {
		this.ammoBag = ammoBag;
	}

	/**
	 * The addAmmo method adds a certain amount of new ammo to the ammoBag;
	 * it keeps an ammo color's max value to 3.
	 * @param ammoToAdd is a map containing the amount of each ammo color to add to the player's ammoBag.
	 */
	public void addAmmo(Map<AmmoColor, Integer> ammoToAdd) {
		ammoToAdd.keySet()
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
	public void consumeAmmo(Map<AmmoColor, Integer> ammoToConsume) {
		ammoToConsume.keySet()
				.forEach(k -> ammoBag.put(k, ammoBag.get(k) - ammoToConsume.get(k)));
	}


	/**
	 * @return tile with the actual player position
	 */
	public Tile getTile() {
		return tile;
	}

	/**
	 * Set the actual player position to the tile passed as argument
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

		if(playerColor != message.getDeadPlayer() && message.getDamageBar().contains(playerColor)) {
			//TODO will need to modify it when GameMode is implmented (no  first attack bonus in FinalFrenzy).
			// first attack bonus
			if(message.getDamageBar().get(FIRST_ATTACKER) == playerColor) {
				score += 1;
			}

			int deaths = message.getDeaths();
			int rank = message.rankedAttackers().indexOf(playerColor);
			if (deaths+rank < message.getValueBar().length) {
				score += message.getValueBar()[deaths+rank];
			}
			else {
				score++;
			}
		}
	}

	public List<Weapon> getWeaponBag() {
		return weaponBag;
	}

	public void addWeapon(Weapon weapon) {
		weaponBag.add(weapon);
		notifyCharacterStateChange();
	}

	public void removeWeapon(Weapon weapon) {
		weaponBag.remove(weapon);
		notifyCharacterStateChange();
	}

	public void setWeaponBag(List<Weapon> weaponBag) {
		this.weaponBag = weaponBag;
		notifyCharacterStateChange();
	}

	public List<PowerUp> getPowerUpBag() {
		return powerUpBag;
	}

	public void addPowerUp(PowerUp powerUp) {
		powerUpBag.add(powerUp);
		notifyCharacterStateChange();
	}

	public void removePowerUp(PowerUp powerUp) {
		powerUpBag.remove(powerUp);
		notifyCharacterStateChange();
	}

	public void setPowerUpBag(List<PowerUp> powerUpBag) {
		this.powerUpBag = powerUpBag;
		notifyCharacterStateChange();
	}

	public int[] getValueBar() {
		return valueBar;
	}

	public void setValueBar(int[] valueBar) {
		this.valueBar = valueBar;
		notifyCharacterStateChange();
	}

	public int getDeaths() {
		return deaths;
	}

	public void setDeaths(int deaths) {
		this.deaths = deaths;
		notifyCharacterStateChange();
	}

	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}


	public void setBeforeFrenzyActivator(boolean beforeFrenzyActivator) {
		this.beforeFrenzyActivator = beforeFrenzyActivator;
	}

	public boolean isBeforeFrenzyActivator() {
		return beforeFrenzyActivator;
	}

	private void notifyCharacterStateChange() {
	    CharacterStateUpdate stateUpdate = new CharacterStateUpdate(this);

	    notifyCharacterStateUpdate(stateUpdate);
    }

	public boolean isFirstSpawn() {
		return firstSpawn;
	}

	public void setFirstSpawn(boolean firstSpawn) {
		this.firstSpawn = firstSpawn;
	}

	public boolean isDead() {
		return damageBar.size() >= 11;
	}
}