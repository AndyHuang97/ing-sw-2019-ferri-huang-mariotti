package it.polimi.SE2019.server.games.player;

import it.polimi.SE2019.server.users.UserData;

/**
 * 
 */
public class Player {

	private boolean active;
	private UserData userData;
	private CharacterState characterState;

	/**
	 * Default constructor
	 * @param active
	 * @param userData
	 * @param characterState
	 */
	public Player(boolean active, UserData userData, CharacterState characterState) {
		this.active = active;
		this.userData = userData;
		this.characterState = characterState;
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

}