package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.util.CommandConstants;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * This condition checks whether a target was selected in a previous action unit.
 *
 * @author andreahuang
 */
public class IsTargetNotInActionUnitTargetList implements Condition {

    private String actionUnitName;

    /**
     * Default constructor. It sets up the action unit from which to look for a target.
     *
     * @param actionUnitName
     */
    public IsTargetNotInActionUnitTargetList(String actionUnitName) {
        this.actionUnitName = actionUnitName;
    }

    /**
     * Checks whether a target was not selected in a previous action unit.
     *
     * @param game the game on which to perform the evaluation.
     * @param targets the targets is the input of the current player. Either tiles or players.
     * @return true if the target was not selected in that action unit, false otherwise.
     */
    @Override
    public boolean check(Game game, Map<String, List<Targetable>> targets) {
        try {
            Logger.getGlobal().warning("IsTargetNotInActionUnitTargetList: " +
                    !game.getActionUnitTargetList(actionUnitName)
                            .stream().anyMatch(t -> targets.get(CommandConstants.TARGETLIST).contains(t)));

            Logger.getGlobal().info("game: " + game.getActionUnitTargetList(actionUnitName).stream()
                    .map(t -> t.getId())
                    .reduce("", (baseString, id) -> baseString + id + ","));
            Logger.getGlobal().info("targets: " + targets.get(CommandConstants.TARGETLIST).stream()
                    .map(t -> t.getId())
                    .reduce("", (baseString, id) -> baseString + id + ","));
            return !game.getActionUnitTargetList(actionUnitName)
                    .stream().anyMatch(t -> targets.get(CommandConstants.TARGETLIST).contains(t));
        } catch (IllegalStateException e) {
            return true;
        }
    }
}
