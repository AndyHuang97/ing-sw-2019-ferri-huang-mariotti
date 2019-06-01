package it.polimi.se2019.server.actions.effects;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.util.CommandConstants;

import java.util.List;
import java.util.Map;

public class DamageTileList extends Damage {

    public DamageTileList(Integer amount) {
        super(amount);
    }

    @Override
    public void run(Game game, Map<String, List<Targetable>> targets) {
        List<Targetable> tileList = targets.get(CommandConstants.TILELIST);

        tileList.stream()
                .forEach(t -> ((Tile) t).getPlayers(game).stream()
                        .forEach(p -> p.getCharacterState().addDamage(game.getCurrentPlayer().getColor(), super.amount)));
    }
}
