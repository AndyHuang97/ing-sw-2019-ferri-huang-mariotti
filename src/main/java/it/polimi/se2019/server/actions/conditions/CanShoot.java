package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.util.CommandConstants;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * The CanShoot class extends the ExecutedActionUnits condition class. It checks whether an action has been already
 * executed or not.
 *
 * @author andreahuang
 */
public class CanShoot extends ExecutedActionUnits {

    private static final int TILEPOSITION = 0;

    /**
     * Default constructor.
     *
     * @param actionUnits the actionUnits on which to evaluate the condition.
     */
    public CanShoot(List<String> actionUnits) {
        super(actionUnits);
    }

    /**
     * Checks the parent condition, if false it checks whether there are any players he/she can shoot.
     *
     * @return a boolean, result of the condition evaluation.
     * @param game the game on which to perform the evaluation.
     * @param targets the targets is the input of the current player. Either tiles or players.
     */
    @Override
    public boolean check(Game game, Map<String, List<Targetable>> targets) {

        if (!super.check(game, targets)) { // if Basic Mode has not been activated yet
            List<Targetable> tileList = targets.get(CommandConstants.TILELIST);
            Tile targetTile = (Tile) tileList.get(TILEPOSITION);

            Logger.getGlobal().info("CanShoot: " +
                    !targetTile.getVisibleTargets(game).stream()
                            .filter(p -> !p.equals(game.getCurrentPlayer()))
                            .collect(Collectors.toList()).isEmpty());
            return !targetTile.getVisibleTargets(game).stream()
                    .filter(p -> !p.equals(game.getCurrentPlayer()))
                    .collect(Collectors.toList()).isEmpty();
        }
        else {
            Logger.getGlobal().info("CanShoot: " + true);
            return true;
        }
    }
}
