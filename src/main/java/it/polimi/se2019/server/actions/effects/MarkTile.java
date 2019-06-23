package it.polimi.se2019.server.actions.effects;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.util.CommandConstants;

import java.util.List;
import java.util.Map;

public class MarkTile implements Effect {

    private static final int POSITION = 0;

    private Integer amount;
    private boolean self;
    private boolean isTile;

    public MarkTile(Integer amount, boolean self, boolean isTile) {
        this.amount = amount;
        this.self = self;
        this.isTile = isTile;
    }

    @Override
    public void run(Game game, Map<String, List<Targetable>> targets) {
        Tile tile;
        if (isTile) {
            if (self) {
                tile = game.getCurrentPlayer().getCharacterState().getTile();
            } else {
                tile = (Tile) targets.get(CommandConstants.TILELIST).get(POSITION);
            }
        } else {
            tile = ((Player)targets.get(CommandConstants.TARGETLIST).get(POSITION))
                    .getCharacterState().getTile();
        }

        List<Player> targetList = tile.getPlayers(game);

        targetList.stream()
                .forEach(p -> p.getCharacterState().addMarker(game.getCurrentPlayer().getColor(), amount));

    }
}
