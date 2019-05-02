package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.player.Player;

public class IsRoomVisible implements Condition {

    private String Color;
    private Player attacker;

    @Override
    public boolean check() {
        return false;
    }
}
