package it.polimi.se2019.server.games.player;

/**
 * 
 */
public enum PlayerColor {
	BLUE("Blue"),
	GREEN("Green"),
	GREY("Grey"),
	PURPLE("Purple"),
	YELLOW("Yellow");

	private String color;

	PlayerColor(String color) {
		this.color = color;
	}


	public String getColor() {
		return color;
	}
}