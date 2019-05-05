package it.polimi.se2019.server.actions.effects;

import it.polimi.se2019.server.games.player.AmmoColor;

public class ReplaceAmmo implements Effect {

    private AmmoColor ammoColor;

    public ReplaceAmmo(AmmoColor ammoColor) {
        this.ammoColor = ammoColor;
    }

    @Override
    public void run() {

    }
}
