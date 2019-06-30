package it.polimi.se2019.server.actions.effects;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.player.AmmoColor;
import it.polimi.se2019.util.CommandConstants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConsumeTargetedAmmo implements Effect {

    private static final int AMMO_POSITION = 0;

    private Integer amount;

    public ConsumeTargetedAmmo(Integer amount) {
        this.amount = amount;
    }

    @Override
    public void run(Game game, Map<String, List<Targetable>> targets) {
        AmmoColor targetedAmmoColor = (AmmoColor) targets.get(CommandConstants.AMMOCOLOR).get(AMMO_POSITION);
        Map<AmmoColor, Integer> ammoToConsume = new HashMap<>();
        ammoToConsume.put(targetedAmmoColor, amount);
        game.getCurrentPlayer().getCharacterState().consumeAmmo(ammoToConsume, game);

    }
}
