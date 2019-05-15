package it.polimi.se2019.server.playerActions;

import it.polimi.se2019.server.exceptions.UnpackingException;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Board;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.graphs.Graph;

import java.util.ArrayList;
import java.util.List;

public class MovePlayerAction extends PlayerAction {

    private List<Tile> moveList = new ArrayList<>();
    private final String errorMessage = "Move action failed";

    public MovePlayerAction(Game game, Player player) {
        super(game, player);
    }

    /**
     * @param params: list param containing Targetable object that can be cast to Tile
     */
    @Override
    public void unpack(List<Targetable> params) throws UnpackingException {
        for (Targetable target : params) {
            try {
                Tile tile = (Tile) target;
                moveList.add(tile);
            } catch (ClassCastException e) {
                throw new UnpackingException();
            }
        }
    }

    @Override
    public void run() {
        Tile arrivalTile = moveList.get(-1);
        getPlayer().getCharacterState().setTile(arrivalTile);
    }

    @Override
    public boolean check() {
        Board board = getGame().getBoard();
        int steps = moveList.size();

        Tile arrivalTile = moveList.get(-1);
        Tile startingTile = getPlayer().getCharacterState().getTile();

        Graph<Tile> graph = board.generateGraph();

        return graph.isReachable(arrivalTile, startingTile, steps);
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }
}
