package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.util.CommandConstants;

import java.util.List;
import java.util.Map;

public class TargetListDistance implements Condition {

    private Integer amount;

    public TargetListDistance(Integer amount) {
        this.amount = amount;
    }

    @Override
    public boolean check(Game game, Map<String, List<Targetable>> targets) {
        Tile attackerTile = game.getCurrentPlayer().getCharacterState().getTile();
        Tile targetTile = null;
        Integer distance = null;
        List<Targetable> targetList = targets.get(CommandConstants.TARGETLIST);
        boolean result = true;

        for (Targetable t : targetList) {
            targetTile = ((Player) t).getCharacterState().getTile();
            distance = game.getBoard().getTileTree().distance(attackerTile, targetTile);
            if(distance == -1 || distance > amount) {
                result = false;
            }
        }
        return result;
    }
}
