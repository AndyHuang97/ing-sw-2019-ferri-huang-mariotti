package it.polimi.se2019.server.actions.effects;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.util.CommandConstants;

import java.util.List;
import java.util.Map;

public class MoveTarget implements Effect {

    private static final int TILEPOSITION = 0;
    private static final int TARGETPOSITION = 0;

    @Override
    public void run(Game game, Map<String, List<Targetable>> targets) {
        Player target = (Player) targets.get(CommandConstants.TARGET).get(TARGETPOSITION);
        Tile tile = (Tile) targets.get(CommandConstants.TILE).get(TILEPOSITION);

        target.getCharacterState().setTile(tile);
    }
}
