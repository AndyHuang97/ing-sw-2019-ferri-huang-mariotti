package it.polimi.se2019.server.actions.effects;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.player.AmmoColor;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class AddAmmo implements Effect {

    private Map<AmmoColor, Integer> ammoToAdd;

    public AddAmmo(Map<AmmoColor, Integer> ammoToAdd) {
        this.ammoToAdd = ammoToAdd;
    }

    @Override
    public void run(Game game, Map<String, List<Targetable>> targets) {
        game.getCurrentPlayer().getCharacterState().addAmmo(ammoToAdd);
    }
}
