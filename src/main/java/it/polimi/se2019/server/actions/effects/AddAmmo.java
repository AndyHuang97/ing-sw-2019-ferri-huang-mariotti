package it.polimi.se2019.server.actions.effects;

import it.polimi.se2019.server.cards.ammo.AmmoColor;
import it.polimi.se2019.server.games.player.Player;

public class AddAmmo implements Effect {

    private AmmoColor ammoColor;
    private Player player;

    public AddAmmo(AmmoColor ammoColor) {
        this.ammoColor = ammoColor;
    }

    @Override
    public void run() {
        player.getCharacterState().updateAmmoBag(ammoColor, 1);
    }
}
