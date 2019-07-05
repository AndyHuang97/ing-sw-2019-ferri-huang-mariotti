package it.polimi.se2019.server.dataupdate;

import it.polimi.se2019.server.cards.ammocrate.AmmoCrate;
import it.polimi.se2019.server.games.board.Board;
import it.polimi.se2019.util.LocalModel;

/**
 * This update message is sent by the model to the views when one the weapon crates is re-spawned. Like all the
 * other update messages the method updateData() must be run to update the local copy of the model in the view.
 *
 * @author Rodolfo Mariotti
 */
public class AmmoCrateUpdate implements StateUpdate {
    private int xPosition;
    private int yPosition;

    private AmmoCrate ammoCrate;

    /**
     * Builds a new AmmoCrateUpdate message. This message contains all the data that the view needs to update his
     * local model.
     *
     * @param xPosition position of the ammo crate on the board, x axis
     * @param yPosition position of the ammo crate on the board, y axis
     * @param ammoCrate reference to the object that needs to be set as ammoCrate
     */
    public AmmoCrateUpdate(int xPosition, int yPosition, AmmoCrate ammoCrate) {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.ammoCrate = ammoCrate;
    }

    /**
     * This method must be run by the views (implements LocalModel) to update their local copy of the model.
     *
     * @param model view's local copy of the model that needs to be updated
     */
    @Override
    public void updateData(LocalModel model) {
        Board board = model.getBoard();

        board.setAmmoCrate(xPosition, yPosition, ammoCrate);
    }
}
