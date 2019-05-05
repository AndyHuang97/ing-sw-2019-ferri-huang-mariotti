package it.polimi.se2019.server.actions.effects;

import it.polimi.se2019.server.games.player.AmmoColor;
import it.polimi.se2019.server.games.player.Player;

public class AddAmmo implements Effect {

    private AmmoColor ammoColor;
    private Player player;
    private Integer amount;

    public AddAmmo(AmmoColor ammoColor, Player player, Integer amount) {
        this.ammoColor = ammoColor;
        this.player = player;
        this.amount = amount;
    }

    @Override
    public void run() {
        player.getCharacterState().updateAmmoBag(ammoColor, amount);
    }
}
