package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.actions.Direction;
import it.polimi.se2019.server.exceptions.TileNotFoundException;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Board;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.net.CommandHandler;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class IsTargetInDirection implements Condition {
    private static final Logger logger = Logger.getLogger(CommandHandler.class.getName());
    @Override
    public boolean check(Game game, Map<String, List<Targetable>> targets) {
        Tile attackerTile = game.getCurrentPlayer().getCharacterState().getTile();
        Player targetPlayer = (Player) targets.get("target").get(0);
        Tile targetTile = targetPlayer.getCharacterState().getTile();
        Direction dir = (Direction) targets.get("direction").get(0);
        Board board = game.getBoard();

        boolean result = false;
        try {
            int[] pos1 = board.getTilePosition(attackerTile);
            int[] pos2 = board.getTilePosition(targetTile);
            switch (dir) {
                case NORTH:
                    result = !((pos1[0] - pos2[0] != 0) || (pos1[1] - pos2[1] < 0));
                    break;
                case EAST:
                    result = !((pos1[0] - pos2[0] > 0) || (pos1[1] - pos2[1] != 0));
                    break;
                case SOUTH:
                    result = !((pos1[0] - pos2[0] != 0) || (pos1[1] - pos2[1] > 0));
                    break;
                case WEST:
                    result = !((pos1[0] - pos2[0] < 0) || (pos1[1] - pos2[1] != 0));
                    break;
                default:
                    break;
            }
        } catch(TileNotFoundException e) {
            logger.warning("Tile not found.");
        }

        return result;
    }
}
