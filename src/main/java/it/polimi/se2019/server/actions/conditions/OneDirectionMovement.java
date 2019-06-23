package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.exceptions.TileNotFoundException;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.util.CommandConstants;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class OneDirectionMovement implements Condition {

    private static final Logger logger = Logger.getLogger(OneDirectionMovement.class.getName());
    private static final int TARGETTILEPOSITION = 0;

    private Integer amount;

    public OneDirectionMovement(Integer amount) {
        this.amount = amount;
    }

    @Override
    public boolean check(Game game, Map<String, List<Targetable>> targets) {
        Tile targetTile = (Tile) targets.get(CommandConstants.TILELIST).get(TARGETTILEPOSITION);
        Tile attackerTile = game.getCurrentPlayer().getCharacterState().getTile();
        boolean result = false;

        if(game.getBoard().getTileTree().isReachable(attackerTile,targetTile, amount)) {
            try {
                int[] targetPos = game.getBoard().getTilePosition(targetTile);
                int[] attackerPos = game.getBoard().getTilePosition(attackerTile);
                if (!(targetPos[0] == attackerPos[0] && targetPos[1] == attackerPos[1]) &&
                        (attackerPos[0] - targetPos[0] == 0 || attackerPos[1] - targetPos[1] == 0)) {
                    result = true;
                }
            } catch (TileNotFoundException e) {
                logger.info("Tile not found");
                return false;
            }
        }
        return result ;
    }
}
