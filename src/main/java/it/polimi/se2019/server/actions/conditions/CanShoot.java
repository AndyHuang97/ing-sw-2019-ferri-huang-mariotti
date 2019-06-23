package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.exceptions.TileNotFoundException;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.util.CommandConstants;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class CanShoot extends ExecutedActionUnits {

    private static final int TILEPOSITION = 0;

    public CanShoot(List<String> actionUnits) {
        super(actionUnits);
    }

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
