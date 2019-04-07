package it.polimi.SE2019.server.actions.conditions;

import it.polimi.SE2019.server.Transaction;
import it.polimi.SE2019.server.games.player.Color;
import it.polimi.SE2019.server.games.player.Player;

public class HasAmmo implements Condition {
    Color color;
    int count;

    public HasAmmo(Color color, int count) {
        this.color = color;
        this.count = count;
    }

    @Override
    public boolean check(Transaction transaction) {
        Player player = transaction.getCurrentPlayer();

        if Player
    }
}