package it.polimi.se2019.server.games.player;

import it.polimi.se2019.server.games.Targetable;

/**
 *
 *
 * @author Rodolfo Mariotti
 */
public enum AmmoColor implements Targetable {
    BLUE("BLUE"),
    RED("RED"),
    YELLOW("YELLOW");

    private String color;

    AmmoColor(String color) {
        this.color = color;
    }

    @Override
    public String getId() {
        return color;
    }

    public String getColor() {
        return color;
    }
}
