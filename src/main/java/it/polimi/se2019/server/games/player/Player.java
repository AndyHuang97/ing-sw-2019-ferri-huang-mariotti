package it.polimi.se2019.server.games.player;

import it.polimi.se2019.server.games.PlayerDeath;
import it.polimi.se2019.server.users.UserData;
import it.polimi.se2019.util.Observer;

/**
 * 
 */
public class Player implements Observer<PlayerDeath> {

	private boolean active;
	private UserData userData;
	private CharacterState characterState;
	private PlayerColor color;

	/**
	 * Default constructor
	 * @param active
	 * @param userData
	 * @param characterState
	 * @param color
	 */
	public Player(boolean active, UserData userData, CharacterState characterState, PlayerColor color) {
		this.active = active;
		this.userData = userData;
		this.characterState = characterState;
		this.color = color;
	}

	/**
	 * @return
	 */
	public boolean getActive() {
		return active;
	}

	/**
	 * @param active
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * @return
	 */
	public UserData getUserData() {
		return userData;
	}

	/**
	 * @param userData
	 */
	public void setUserData(UserData userData) {
		this.userData = userData;
	}

	/**
	 * @return characterState
	 */
	public CharacterState getCharacterState() {
		return characterState;
	}

	/**
	 * @param characterState
	 */
	public void setCharacterState(CharacterState characterState) {
		this.characterState = characterState;
	}

	public PlayerColor getColor() {
		return color;
	}

	public void setColor(PlayerColor color) {
		this.color = color;
	}

	@Override
	public void update(PlayerDeath message) {

	}
}