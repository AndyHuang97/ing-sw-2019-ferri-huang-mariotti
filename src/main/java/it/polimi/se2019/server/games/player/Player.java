package it.polimi.se2019.server.games.player;

import it.polimi.se2019.server.games.PlayerDeath;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.users.UserData;
import it.polimi.se2019.util.Observable;
import it.polimi.se2019.util.Observer;
import it.polimi.se2019.util.Response;

/**
 * This class represent the player. It's an observer waiting for PlayerDeath objects in order to update his score.
 * The data about the actual player (the person who is playing the game) is contained in userData and the data about
 * the character can be found in characterState.
 *
 * @author Rodolfo Mariotti
 */
public class Player extends Observable<Response> implements Targetable, Observer<PlayerDeath> {

	private UserData userData;
	private CharacterState characterState;
	private PlayerColor color;
	private String id;


    /**
     * This constructor creates an almost empty Player object.
     * This method is used in some tests to create placeholder players.
     *
     * @param name name of the player
     */
	public Player(String name) {
		this.userData = new UserData(name);
	}

	/**
	 * This constructor is used by GameManager during the initialization of the game to create new players.
     *
	 * @param userData reference to the UserData that will be part of the new player instance (usually a new instance
     *                 as of UserData as well)
	 * @param characterState reference to the CharacterState of the new player
	 * @param color color of the new player
	 */
	public Player(String id, boolean active, UserData userData, CharacterState characterState, PlayerColor color) {
		this.userData = userData;
		this.id = id;
		this.characterState = characterState;
		this.color = color;
	}

	@Override
	public String getId() {
		return id;
	}

    /**
     * Setter method for the id attribute.
     *
     * @param id new id of this object
     */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return true if the player is active, false otherwise
	 */
	public boolean getActive() {
		return characterState.isConnected();
	}

	/**
	 * @param active
	 */
	public void setActive(boolean active) {
		characterState.setConnected(active);
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
    public void update(PlayerDeath playerDeath) {
        characterState.updateScore(playerDeath, color);
    }
}