package it.polimi.se2019.server.users;

/**
 * This class represent the user (as opposed to the character).
 * Contains the user nickname, while designing the model this class had more function but at this state the refactor
 * would be too long.
 *
 * @author Rodolfo Mariotti
 */
public class UserData {

	private String nickname;

	/**
     * Builds an UserData object and sets the nickname.
     *
	 * @param nickname user's nickname
	 */
	public UserData(String nickname) {
		this.nickname = nickname;
	}

	/**
     * Getter method for the nickname attribute.
	 *
	 * @return nickname user's nickname
	 */
	public String getNickname() {
		return nickname;
	}

	/**
	 * Setter method for the nickname attribute.
     *
	 * @param nickname reference to the string that will be set as nickname
	 */
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
}