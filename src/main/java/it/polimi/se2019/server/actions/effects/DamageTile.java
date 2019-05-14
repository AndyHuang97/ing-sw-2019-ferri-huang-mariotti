package it.polimi.se2019.server.actions.effects;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.Player;

import java.util.List;
import java.util.Map;

public class DamageTile implements Effect {

    private Integer amount;

    public DamageTile(Integer amount) {
        this.amount = amount;
    }

    @Override
    public void run(Game game, Map<String, List<Targetable>> targets) {
        Tile tile = (Tile) targets.get("tile").get(0);

        List<Player> targetList = tile.getPlayers(game);

        targetList.stream()
                .forEach(p -> p.getCharacterState().addDamage(game.getCurrentPlayer().getColor(), amount));

    }
}
