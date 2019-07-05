package it.polimi.se2019.server.actions.effects;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.util.CommandConstants;

import java.util.List;
import java.util.Map;

/**
 * This effect marks all the players in the indicated  tile.
 *
 * @author andreahuang
 *
 */
public class MarkTile implements Effect {

    private static final int POSITION = 0;

    private Integer amount;
    private boolean self;
    private boolean isTile;

    /**
     * Default constructor. It sets up the amount of damage to inflict, and it also has an additional
     * name of action unit for correct targeting.
     *
     * @param amount is the amount of damage to inflict.
     * @param self is a boolean that indicates whether the tile to shoot is the attacker's or another target's tile.
     * @param isTile indicates whether the tile to evaluate is an actual tile or one from a player target.
     */
    public MarkTile(Integer amount, boolean self, boolean isTile) {
        this.amount = amount;
        this.self = self;
        this.isTile = isTile;
    }

    /**
     * This effect adds markers to the players in the chosen tile.
     *
     * @param  game the game on which to perform the effect.
     * @param targets the targets is the input of the current player. Either tiles or players.
     */
    @Override
    public void run(Game game, Map<String, List<Targetable>> targets) {
        Tile tile;
        if (isTile) {
            if (self) {
                tile = game.getCurrentPlayer().getCharacterState().getTile();
            } else {
                tile = (Tile) targets.get(CommandConstants.TILELIST).get(POSITION);
            }
        } else {
            tile = ((Player)targets.get(CommandConstants.TARGETLIST).get(POSITION))
                    .getCharacterState().getTile();
        }

        List<Player> targetList = tile.getPlayers(game);

        targetList.stream()
                .forEach(p -> p.getCharacterState().addMarker(game.getCurrentPlayer().getColor(), amount));

    }
}
