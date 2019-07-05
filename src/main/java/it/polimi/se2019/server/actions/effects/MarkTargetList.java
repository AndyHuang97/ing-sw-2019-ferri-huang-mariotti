package it.polimi.se2019.server.actions.effects;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.util.CommandConstants;

import java.util.List;
import java.util.Map;

/**
 * This effect deals markers to a player target list.
 *
 * @author andreahuang
 *
 */
public class MarkTargetList implements Effect {

    private Integer amount;

    /**
     * Default constructor. It sets up the amount of markers to inflict to players.
     *
     * @param amount is the amount of damage to inflict.
     */
    public MarkTargetList(Integer amount) {
        this.amount = amount;
    }

    /**
     * This method adds the markers to players in the target list.
     *
     * @param  game the game on which to perform the effect.
     * @param targets the targets is the input of the current player. Either tiles or players.
     */
    @Override
    public void run(Game game, Map<String, List<Targetable>> targets) {
        List<Targetable> targetList = targets.get(CommandConstants.TARGETLIST);

        targetList.stream()
                .forEach(p -> {
                    ((Player) p).getCharacterState().addMarker(game.getCurrentPlayer().getColor(), amount);
                    game.getCumulativeTargetSet().add(p);
                });

    }
}
