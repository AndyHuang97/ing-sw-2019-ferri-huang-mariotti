package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.player.Player;

public class IsPlayerNotVisible implements Condition {

    private Player target, attacker;

    @Override
    public boolean check() {
        return false;
    }
}
