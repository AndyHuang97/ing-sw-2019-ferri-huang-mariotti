package it.polimi.se2019.server.games.player;

/**
 * This enumerations contains all the colors available for the players. GameManager will assign a color to each
 * player. A player can be identified by his color.
 *
 * @author Rodolfo Mariotti
 */
public enum PlayerColor {
	BLUE("Blue"),
	GREEN("Green"),
	GREY("Grey"),
	PURPLE("Purple"),
	YELLOW("Yellow");

	private String color;

    /**
     * Constructor for the PlayerColor objects.
     *
     * @param color a string with the color name
     */
	PlayerColor(String color) {
		this.color = color;
	}


    /**
     * Getter method for the color attributes
     *
     * @return the name of the color
     */
	public String getColor() {
		return color;
	}
}