package it.polimi.se2019.server.actions.effects;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.Player;

import java.util.List;
import java.util.Map;

public class DamageTargetsAtDistance extends Damage {

    private Integer distance;

    protected DamageTargetsAtDistance(Integer amount, String actionUnitName) {
        super(amount, actionUnitName);
    }

    @Override
    public void run(Game game, Map<String, List<Targetable>> targets) {
        Tile attackerTile = game.getCurrentPlayer().getCharacterState().getTile();
        List<Player> targetList = game.getBoard().getPlayersAtDistance(game, attackerTile, distance);

        targetList.forEach(p ->
                p.getCharacterState().addDamage(game.getCurrentPlayer().getColor(), amount, game));

    }
}
