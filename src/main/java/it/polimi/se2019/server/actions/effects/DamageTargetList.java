package it.polimi.se2019.server.actions.effects;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.player.Player;

import java.util.List;
import java.util.Map;

public class DamageTargetList implements Effect {

    private Integer amount;

    public DamageTargetList(Integer amount) {
        this.amount = amount;
    }

    @Override
    public void run(Game game, Map<String, List<Targetable>> targets) {
        List<Targetable> targetList = targets.get("targetList");

        targetList.stream()
                .forEach(p -> ((Player) p).getCharacterState().addDamage(game.getCurrentPlayer().getColor(), amount));

    }
}
