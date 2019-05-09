package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.exceptions.TileNotFoundException;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.net.CommandHandler;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class OneDirectionMovement implements Condition {

    private static final Logger logger = Logger.getLogger(CommandHandler.class.getName());
    private Tile initialPos, finalPos;

    public OneDirectionMovement(Tile initialPos, Tile finalPos) {
        this.initialPos = initialPos;
        this.finalPos = finalPos;
    }

    @Override
    public boolean check(Game game, Map<String, List<Targetable>> targets) {
        Tile targetTile = (Tile) targets.get("tile").get(0);
        Tile attackerTile = game.getCurrentPlayer().getCharacterState().getTile();
        boolean result = false;
        if(game.getBoard().generateGraph().isReachable(attackerTile, targetTile, 2)) {
            try {
                int[] targetPos = game.getBoard().getTilePosition(targetTile);
                int[] attackerPos = game.getBoard().getTilePosition(attackerTile);
                if (!targetPos.equals(attackerPos) &&
                        (attackerPos[0] - targetPos[0] == 0 || attackerPos[1] - targetPos[1] == 0)) {
                    result = true;
                }
            } catch (TileNotFoundException e) {
                logger.info("Tile not found");
            }
        }
        return result ;
    }
}
