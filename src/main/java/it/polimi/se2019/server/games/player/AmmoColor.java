package it.polimi.se2019.server.games.player;

import it.polimi.se2019.server.games.Targetable;

/**
 *This enum contains the possible colors for the ammp.
 *
 * @author Rodolfo Mariotti
 */
public enum AmmoColor implements Targetable {
    BLUE("BLUE"),
    RED("RED"),
    YELLOW("YELLOW");

    private String color;


    /**
     * Constructor for AmmoColors objects.
     *
     * @param color a string with the color name
     */
    AmmoColor(String color) {
        this.color = color;
    }

    @Override
    public String getId() {
        return color;
    }

    /**
     * Getter method for the color attributee.
     *
     * @return the name of the color
     */
    public String getColor() {
        return color;
    }
}
