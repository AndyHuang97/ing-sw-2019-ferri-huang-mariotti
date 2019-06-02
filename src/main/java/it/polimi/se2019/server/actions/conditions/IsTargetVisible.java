package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.util.CommandConstants;

import java.util.List;
import java.util.Map;

public class IsTargetVisible implements Condition {

    private static final int PLAYERPOSITION = 0;

    @Override
    public boolean check(Game game, Map<String, List<Targetable>> targets) {
        Player targetPlayer = (Player) targets.get(CommandConstants.TARGET).get(PLAYERPOSITION);
        Tile attackerTile = game.getCurrentPlayer().getCharacterState().getTile();

        return attackerTile.getVisibleTargets(game).contains(targetPlayer);
    }
}
