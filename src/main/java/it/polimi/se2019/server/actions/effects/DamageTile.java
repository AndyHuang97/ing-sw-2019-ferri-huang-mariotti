package it.polimi.se2019.server.actions.effects;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.util.CommandConstants;

import java.util.List;
import java.util.Map;

public class DamageTile extends Damage {

    private static final int TILEPOSITION = 0;

    public DamageTile(Integer amount) {
        super(amount);
    }

    @Override
    public void run(Game game, Map<String, List<Targetable>> targets) {
        Tile tile = (Tile) targets.get(CommandConstants.TILE).get(TILEPOSITION);

        List<Player> targetList = tile.getPlayers(game);

        targetList.stream()
                .forEach(p -> p.getCharacterState().addDamage(game.getCurrentPlayer().getColor(), super.amount));

    }
}
