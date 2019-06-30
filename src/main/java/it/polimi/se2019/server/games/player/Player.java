package it.polimi.se2019.server.games.player;

import it.polimi.se2019.server.dataupdate.CharacterStateUpdate;
import it.polimi.se2019.server.dataupdate.PlayerEventListener;
import it.polimi.se2019.server.dataupdate.StateUpdate;
import it.polimi.se2019.server.games.PlayerDeath;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.users.UserData;
import it.polimi.se2019.util.Observable;
import it.polimi.se2019.util.Response;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class Player extends Observable<Response> implements Targetable, PlayerEventListener {

	private boolean active;
	private UserData userData;
	private CharacterState characterState;
	private PlayerColor color;
	private String id;


	public Player(String name) {
		this.userData = new UserData(name);
	}

	/**
	 * Default constructor
	 * @param active
	 * @param userData
	 * @param characterState
	 * @param color
	 */
	public Player(String id, boolean active, UserData userData, CharacterState characterState, PlayerColor color) {
		this.active = active;
		this.userData = userData;
		this.id = id;
		this.characterState = characterState;
		this.color = color;
	}

	@Override
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return true if the player is active, false otherwise
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
    public void onCharacterStateUpdate(CharacterStateUpdate characterStateUpdate) {
	    // Send a Response to the Game telling that CharacterState of this Player changed
        List<StateUpdate> updateList = new ArrayList<>();
        updateList.add(characterStateUpdate);
        Response response = new Response(updateList);

        notify(response);
    }

    @Override
    public void onPlayerDeath(PlayerDeath playerDeath) {
	    characterState.updateScore(playerDeath, color);
    }
}