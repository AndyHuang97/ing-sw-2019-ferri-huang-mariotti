package it.polimi.se2019.server.actions.effects;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.Player;

import java.util.List;
import java.util.Map;

/**
 * This effect damages all players at the indicated distance.
 *
 * @author andreahuang
 *
 */
public class DamageTargetsAtDistance extends Damage {

    private Integer distance;

    /**
     * Default constructor. It sets up the amount of damage to inflict, and it also has an additional
     * name of action unit for correct targeting.
     *
     * @param amount is the amount of damage to inflict.
     * @param actionUnitName the action unit used for correct targeting.
     */
    protected DamageTargetsAtDistance(Integer amount, String actionUnitName) {
        super(amount, actionUnitName);
    }

    /**
     * This method adds damage to all players at a certain distance.
     *
     * @param  game the game on which to perform the effect.
     * @param targets the targets is the input of the current player. Either tiles or players.
     */
    @Override
    public void run(Game game, Map<String, List<Targetable>> targets) {
        Tile attackerTile = game.getCurrentPlayer().getCharacterState().getTile();
        List<Player> targetList = game.getBoard().getPlayersAtDistance(game, attackerTile, distance);

        targetList.forEach(p ->
                p.getCharacterState().addDamage(game.getCurrentPlayer().getColor(), amount, game));

    }
}
