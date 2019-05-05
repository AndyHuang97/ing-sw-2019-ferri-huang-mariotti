package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.player.Player;

public class PlayerNotInRoom implements Condition {

    private Player attacker;
    private String Color;

    @Override
    public boolean check() {
        return false;
    }
}
