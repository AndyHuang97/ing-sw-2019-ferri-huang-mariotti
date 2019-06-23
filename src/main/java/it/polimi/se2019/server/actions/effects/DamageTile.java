package it.polimi.se2019.server.actions.effects;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.util.CommandConstants;

import java.util.List;
import java.util.Map;

public class DamageTile extends Damage {

    private static final int TILEPOSITION = 0;

    private boolean self;
    private Integer tileIndex;

    protected DamageTile(Integer amount, String actionUnitName, boolean self) {
        super(amount, actionUnitName);
        this.self = self;
    }

    @Override
    public void run(Game game, Map<String, List<Targetable>> targets) {
        Tile tile;
        if (actionUnitName == null) {
            if (tileIndex == null) {
                if (self) {
                    tile = game.getCurrentPlayer().getCharacterState().getTile();
                } else {
                    tile = (Tile) targets.get(CommandConstants.TILELIST).get(TILEPOSITION);
                }
            } else {
                tile = (Tile) targets.get(CommandConstants.TILELIST).get(tileIndex);
            }
        } else {
            tile = (Tile) game.getActionUnit(actionUnitName).getCommands()
                    .get(CommandConstants.OLDTILELIST).get(TILEPOSITION);
        }

        List<Player> targetList = tile.getPlayers(game);

        targetList.stream()
                .filter(p -> !p.equals(game.getCurrentPlayer()))
                .forEach(p -> p.getCharacterState().addDamage(game.getCurrentPlayer().getColor(), super.amount));

    }
}
