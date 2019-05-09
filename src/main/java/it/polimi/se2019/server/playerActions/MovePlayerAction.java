package it.polimi.se2019.server.playerActions;

import it.polimi.se2019.server.exceptions.UnpackingException;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.Player;

import java.util.List;

public class MovePlayerAction extends PlayerAction {

    Tile finalTile;
    Integer steps;

    public MovePlayerAction(Game game, Player player) {
        super(game, player);
    }

    /**
     * @param params: list param containing at index 0 Targetable object that can be cast to Tile,
     *                at index 1 an int representing the steps
     */
    @Override
    public void unpack(List<Targetable> params) throws UnpackingException {
        finalTile = (Tile) params.get(0);
        steps = (Integer) params.get(1);
    }

    @Override
    public void run() {

    }
}
