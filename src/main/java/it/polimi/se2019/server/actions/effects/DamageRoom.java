package it.polimi.se2019.server.actions.effects;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.util.CommandConstants;

import java.util.List;
import java.util.Map;

/**
 * This effect damages a whole room with the indicated damage amount.
 *
 * @author andreahuang
 *
 */
public class DamageRoom extends Damage {

    private static final int TILEPOSITION = 0;

    /**
     * Default constructor. It sets up the indicated amount of damage to inflict.
     *
     * @param amount the amount of damage to inflict.
     */
    public DamageRoom(Integer amount) {
        super(amount, null);
    }

    /**
     * This method adds damage for all the players in the room.
     *
     * @param  game the game on which to perform the effect.
     * @param targets the targets is the input of the current player. Either tiles or players.
     */
    @Override
    public void run(Game game, Map<String, List<Targetable>> targets) {
        Tile tile = (Tile) targets.get(CommandConstants.TILELIST).get(TILEPOSITION);
        List<Tile> room = tile.getRoom(game.getBoard());

        room.forEach(t -> t.getPlayers(game).stream()
                        .filter(p -> !p.equals(game.getCurrentPlayer()))
                        .forEach(p -> p.getCharacterState().addDamage(game.getCurrentPlayer().getColor(), super.amount, game)));
    }
}
