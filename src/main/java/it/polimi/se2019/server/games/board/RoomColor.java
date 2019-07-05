package it.polimi.se2019.server.games.board;

import it.polimi.se2019.server.games.Targetable;

/**
 * This enumeration contains values for the color attribute of the Tile class.
 *
 * @author Rodolfo Mariotti
 */
public enum RoomColor implements Targetable {
    BLUE("BLUE"),
    PURPLE("PURPLE"),
    RED("RED"),
    WHITE("WHITE"),
    YELLOW("YELLOW"),
    GREEN("GREEN");

    private String color;

    /**
     * This constructor it's used to build enumeration values.
     *
     * @param color color of a tile
     */
    RoomColor(String color) {
        this.color = color;
    }

    /**
     * Getter method for the color attribute.
     *
     * @return color of a tile
     */
    public String getColor() {
        return color;
    }

    @Override
    public String getId() {
        return null;
    }
}
