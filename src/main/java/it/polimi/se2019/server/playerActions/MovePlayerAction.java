package it.polimi.se2019.server.playerActions;

import it.polimi.se2019.client.util.Constants;
import it.polimi.se2019.server.cards.Card;
import it.polimi.se2019.server.exceptions.UnpackingException;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.util.ErrorResponse;

import java.util.ArrayList;
import java.util.List;

public class MovePlayerAction extends PlayerAction {
    private static final String ERRORMESSAGE = "Move action failed";
    private static final int TILEPOSITION = 0;

    private List<Tile> moveList = new ArrayList<>();

    public MovePlayerAction(Game game, Player player) {
        super(game, player);
    }
    public MovePlayerAction(int amount) { super(amount);}

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
        Tile arrivalTile = moveList.get(TILEPOSITION);
        getPlayer().getCharacterState().setTile(arrivalTile);

        // unset the VirtualPlayerPosition so no wrong values of VirtualPlayerPosition are left in the system
        getGame().setVirtualPlayerPosition(null);
    }

    @Override
    public boolean check() {
        /*
        Board board = getGame().getBoard();

        Tile arrivalTile = moveList.get(0);
        Tile startingTile = getPlayer().getCharacterState().getTile();

        Graph<Tile> graph = board.generateGraph();

        return graph.isReachable(arrivalTile, startingTile, steps);

         */

        Tile arrivalTile = moveList.get(TILEPOSITION);

        // set the VirtualPlayerPosition so that GrabPlayerAction can access the final position during check phase
        getGame().setVirtualPlayerPosition(arrivalTile);

        return true;
    }

    @Override
    public ErrorResponse getErrorMessage() {
        return new ErrorResponse(ERRORMESSAGE);
    }

    @Override
    public Card getCard() {
        return null;
    }

    public List<Tile> getMoveList() {
        return moveList;
    }

    @Override
    public String getId() {
        return Constants.MOVE;
    }
}
