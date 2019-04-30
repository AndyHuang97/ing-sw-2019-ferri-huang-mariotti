package it.polimi.se2019.server.actions.effects;

import it.polimi.se2019.server.cards.ammo.AmmoColor;

public class AsAmmo implements Effect {

    private AmmoColor ammoColor;

    public AsAmmo(AmmoColor ammoColor) {
        this.ammoColor = ammoColor;
    }

    @Override
    public void run() {

    }
}
