package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.actions.Direction;
import it.polimi.se2019.server.exceptions.TileNotFoundException;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Board;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.Player;

import java.util.List;
import java.util.Map;

public class IsTargetInDirection implements Condition {
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
                    if(pos1[0] - pos2[0] != 0) {
                        result = false;
                    }
                    if(pos1[1] - pos2[1] < 0) {
                        result = false;
                    }
                    result = true;
                    break;
                case EAST:
                    if(pos1[0] - pos2[0] > 0) {
                        result = false;
                    }
                    if(pos1[1] - pos2[1] != 0) {
                        result = false;
                    }
                    result = true;
                    break;
                case SOUTH:
                    if(pos1[0] - pos2[0] != 0) {
                        result = false;
                    }
                    if(pos1[1] - pos2[1] > 0) {
                        result = false;
                    }
                    result = true;
                    break;
                case WEST:
                    if(pos1[0] - pos2[0] < 0) {
                        result = false;
                    }
                    if(pos1[1] - pos2[1] != 0) {
                        result = false;
                    }
                    result = true;
                    break;
                default:
                    break;
            }
        } catch(TileNotFoundException e) {
            System.out.println("Tile not found.");
        }

        return result;
    }
}
