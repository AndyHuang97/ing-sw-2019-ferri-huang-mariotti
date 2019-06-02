package it.polimi.se2019.server.actions.effects;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.util.CommandConstants;

import java.util.List;
import java.util.Map;

public class DamageTargetList extends Damage {

    public DamageTargetList(Integer amount) {
        super(amount);
    }

    @Override
    public void run(Game game, Map<String, List<Targetable>> targets) {
        List<Targetable> targetList = targets.get(CommandConstants.TARGETLIST);

        targetList.stream()
                .forEach(p -> ((Player) p).getCharacterState().addDamage(game.getCurrentPlayer().getColor(), super.amount));

    }
}
