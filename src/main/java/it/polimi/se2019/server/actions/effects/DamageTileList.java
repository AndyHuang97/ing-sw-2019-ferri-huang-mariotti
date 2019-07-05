package it.polimi.se2019.server.actions.effects;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.util.CommandConstants;

import java.util.List;
import java.util.Map;

/**
 * This effect damages all the targets contained in the tiles of the tile list of a given amount.
 *
 */
public class DamageTileList extends Damage {

    /**
     * Default constructor. It sets up the damage to inflict to players.
     *
     * @param amount is the amount of damage to inflict.
     */
    public DamageTileList(Integer amount) {
        super(amount, null);
    }

    /**
     * This method inflicts damage to all the players in the selected tile list.
     *
     * @param  game the game on which to perform the effect.
     * @param targets the targets is the input of the current player. Either tiles or players.
     */
    @Override
    public void run(Game game, Map<String, List<Targetable>> targets) {
        List<Targetable> tileList = targets.get(CommandConstants.TILELIST);

        tileList.forEach(t -> ((Tile) t).getPlayers(game)
                .forEach(p -> p.getCharacterState().addDamage(game.getCurrentPlayer().getColor(), super.amount, game)));
    }
}
