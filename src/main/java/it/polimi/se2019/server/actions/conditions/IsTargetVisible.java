package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.Player;

import java.util.List;
import java.util.Map;

public class IsTargetVisible implements Condition {

    private Tile tile;
    private Player target;

    public IsTargetVisible(Tile tile, Player target) {
        this.tile = tile;
        this.target = target;
    }


    @Override
    public boolean check(Game game, Map<String, List<Targetable>> targets) {
        Player targetPlayer = (Player) targets.get("target").get(0);
        Tile attackerTile = game.getCurrentPlayer().getCharacterState().getTile();

        return attackerTile.getVisibleTargets(game).contains(targetPlayer);
    }
}
