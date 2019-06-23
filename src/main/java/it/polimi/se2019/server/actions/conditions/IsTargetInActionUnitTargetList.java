package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.util.CommandConstants;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class IsTargetInActionUnitTargetList implements Condition {

    private String actionUnitName;

    public IsTargetInActionUnitTargetList(String actionUnitName) {
        this.actionUnitName = actionUnitName;
    }

    @Override
    public boolean check(Game game, Map<String, List<Targetable>> targets) {
        try {
            Logger.getGlobal().warning("IsTargetInActionUnitTargetList: " +
                    game.getActionUnitTargetList(actionUnitName)
                            .stream().anyMatch(t -> targets.get(CommandConstants.TARGETLIST).contains(t)));
            Logger.getGlobal().info("game: "+game.getActionUnitTargetList(actionUnitName).stream()
                    .map(t -> t.getId())
                    .reduce("", (baseString, id) -> baseString + id + ","));
            Logger.getGlobal().info("targets: "+targets.get(CommandConstants.TARGETLIST).stream()
                    .map(t -> t.getId())
                    .reduce("", (baseString, id) -> baseString + id + ","));
            return game.getActionUnitTargetList(actionUnitName)
                    .stream().anyMatch(t -> targets.get(CommandConstants.TARGETLIST).contains(t));
        } catch (IllegalStateException e){
            return false;
        }
    }
}
