package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.player.AmmoColor;
import it.polimi.se2019.server.games.player.Player;

public class HasAmmo implements Condition {

    private AmmoColor ammoColor;
    private int amount;
    private Player player;

    public HasAmmo(AmmoColor ammoColor, int amount, Player player) {
        this.ammoColor = ammoColor;
        this.amount = amount;
        this.player = player;
    }

    @Override
    public boolean check() {
    /*    Player player = transaction.getCurrentPlayer();

        if Player
        */
    return false;
    }
}