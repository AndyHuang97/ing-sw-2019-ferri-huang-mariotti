package it.polimi.se2019.server.actions;

import it.polimi.se2019.server.games.Targetable;

public enum Direction implements Targetable {
    NORTH,
    EAST,
    SOUTH,
    WEST;

    @Override
    public String getId() {
        return null;
    }
}
