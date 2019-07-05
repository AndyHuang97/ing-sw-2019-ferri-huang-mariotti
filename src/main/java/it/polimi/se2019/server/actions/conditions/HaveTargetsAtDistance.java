package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * The HaveTargetsAtDistance condition class checks the presence of any targets at a certain distance.
 *
 * @author andreahuang
 */
public class HaveTargetsAtDistance implements Condition {

    private Integer distance;

    /**
     * Default constructor.
     *
     * @param distance the distance at which the check is performed
     */
    public HaveTargetsAtDistance(Integer distance) {
        this.distance = distance;
    }

    /**
     * Checks whether any player is present at a certain distance from the attacker.
     *
     * @param game the game on which to perform the evaluation.
     * @param targets the targets is the input of the current player. Either tiles or players.
     * @return
     */
    @Override
    public boolean check(Game game, Map<String, List<Targetable>> targets) {
        Tile attackerTile = game.getCurrentPlayer().getCharacterState().getTile();

        List<Player> targetList = game.getBoard().getPlayersAtDistance(game, attackerTile, distance);

        Logger.getGlobal().info("HaveTargetsAtDistance: "+!targetList.isEmpty());
        return !targetList.isEmpty();
    }

}
