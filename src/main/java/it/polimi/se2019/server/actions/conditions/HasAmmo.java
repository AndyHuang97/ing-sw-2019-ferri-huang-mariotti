package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.cards.ammo.AmmoColor;

public class HasAmmo implements Condition {

    AmmoColor color;
    int amount;

    public HasAmmo(AmmoColor color, int amount) {
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