package it.polimi.se2019.server.actions.effects;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.util.CommandConstants;

import java.util.List;
import java.util.Map;

public class DamageRoom extends Damage {

    private static final int TILEPOSITION = 0;

    public DamageRoom(Integer amount) {
        super(amount, null);
    }

    @Override
    public void run(Game game, Map<String, List<Targetable>> targets) {
        Tile tile = (Tile) targets.get(CommandConstants.TILELIST).get(TILEPOSITION);
        List<Tile> room = tile.getRoom(game.getBoard());

        room.stream()
                .forEach(t -> t.getPlayers(game).stream()
                        .filter(p -> !p.equals(game.getCurrentPlayer()))
                        .forEach(p -> p.getCharacterState().addDamage(game.getCurrentPlayer().getColor(), super.amount, game)));
    }
}
