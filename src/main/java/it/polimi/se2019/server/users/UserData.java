package it.polimi.se2019.server.users;

/**
 * 
 */
public class UserData {

	private String nickname;

	/**
	 * Default constructor
	 * @param nickname
	 */
	public UserData(String nickname) {
		this.nickname = nickname;
	}

	/**
	 *
	 * @return nickname
	 */
	public String getNickname() {
		return nickname;
	}

	/**
	 *
	 * @param nickname
	 */
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
}