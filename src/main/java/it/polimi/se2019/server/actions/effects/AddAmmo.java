package it.polimi.se2019.server.actions.effects;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.player.AmmoColor;

import java.util.List;
import java.util.Map;

public class AddAmmo implements Effect {

    private AmmoColor ammoColor;
    private Integer amount;

    public AddAmmo(AmmoColor ammoColor, Integer amount) {
        this.ammoColor = ammoColor;
        this.amount = amount;
    }

    @Override
    public void run(Game game, Map<String, List<Targetable>> targets) {
        game.getCurrentPlayer().getCharacterState().updateAmmoBag(ammoColor, amount);
    }
}
