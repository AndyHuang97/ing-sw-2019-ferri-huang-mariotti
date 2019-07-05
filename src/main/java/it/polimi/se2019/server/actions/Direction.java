package it.polimi.se2019.server.actions;

import it.polimi.se2019.server.games.Targetable;

/**
 * Direction as an enumeration
 *
 * @author FF
 *
 */
public enum Direction implements Targetable {
    NORTH,
    EAST,
    SOUTH,
    WEST;

    /**
     * No id is available
     *
     * @return null always
     *
     */
    @Override
    public String getId() {
        return null;
    }
}
