package it.polimi.se2019.server.playeractions;

import it.polimi.se2019.client.util.Constants;
import it.polimi.se2019.server.cards.Card;
import it.polimi.se2019.server.exceptions.UnpackingException;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represent a move action. Like all the PlayerAction can be run and checked, his methods
 * should be run by the Controller.
 *
 * @author Andrea Huang
 */
public class MovePlayerAction extends PlayerAction {
    private static final String ERRORMESSAGE = "Move action failed";
    private static final int TILEPOSITION = 0;

    private List<Tile> moveList = new ArrayList<>();

    public MovePlayerAction(Game game, Player player) {
        super(game, player);
    }
    public MovePlayerAction(int amount) { super(amount);}

    /**
     * Unpack the params argument into the object.
     *
     * @requires (* dynamic type of each element of param is Tile *);
     * @param params: list of Targetable objects of dynamic type Tile
     * @throws UnpackingException if one of the Targetable in the params is not a Tile
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
        Tile arrivalTile = moveList.get(TILEPOSITION);

        // set the VirtualPlayerPosition so that GrabPlayerAction can access the final position during check phase
        getGame().setVirtualPlayerPosition(arrivalTile);

        return true;
    }

    @Override
    public Card getCard() {
        return null;
    }

    /**
     * Getter method for the moveList attribute.
     *
     * @return the moveList containing all the Tile selected by the player
     */
    public List<Tile> getMoveList() {
        return moveList;
    }

    @Override
    public String getId() {
        return Constants.MOVE;
    }
}
