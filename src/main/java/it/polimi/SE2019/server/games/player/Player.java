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

		return false;
	}

	/**
	 * @param value
	 */
	public void setActive(boolean value) {

	}

	/**
	 * @return
	 */
	public UserData getUserData() {

		return null;
	}

	/**
	 * @param value
	 */
	public void setUserData(UserData value) {

	}

	/**
	 * @return
	 */
	public CharacterState getCharacterState() {

		return null;
	}

	/**
	 * @param value
	 */
	public void setCharacterState(CharacterState value) {

	}

}