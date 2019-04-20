package it.polimi.SE2019.server.actions.conditions;

import it.polimi.SE2019.server.Transaction;
import it.polimi.SE2019.server.games.player.Color;
import it.polimi.SE2019.server.games.player.Player;

public class HasAmmo implements Condition {
    Color color;
    int amount;

    public HasAmmo(Color color, int amount) {
        this.color = color;
        this.amount = amount;
    }

    @Override
    public boolean check() {
    /*    Player player = transaction.getCurrentPlayer();

        if Player
        */
    return false;
    }
}