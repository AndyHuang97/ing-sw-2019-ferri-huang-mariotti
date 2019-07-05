package it.polimi.se2019.server.actions.effects;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.util.CommandConstants;

import java.util.List;
import java.util.Map;

/**
 * This effect add markers to players in the tile list.
 *
 * @author andreahuang
 *
 */
public class MarkTileList implements Effect {

    private Integer amount;

    /**
     * Default constructor. It sets up the markers to inflict to players.
     *
     * @param amount is the amount of damage to inflict.
     */
    public MarkTileList(Integer amount) {
        this.amount = amount;
    }

    /**
     * This method adds markers to the players contained in the tiles of the tile list.
     *
     * @param  game the game on which to perform the effect.
     * @param targets the targets is the input of the current player. Either tiles or players.
     */
    @Override
    public void run(Game game, Map<String, List<Targetable>> targets) {

        List<Targetable> tileList = targets.get(CommandConstants.TILELIST);

        tileList.stream()
                .forEach(t -> ((Tile) t).getPlayers(game).stream()
                        .forEach(p -> p.getCharacterState().addMarker(game.getCurrentPlayer().getColor(), amount)));
    }

}
