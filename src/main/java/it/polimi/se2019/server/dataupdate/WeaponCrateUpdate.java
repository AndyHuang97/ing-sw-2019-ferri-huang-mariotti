package it.polimi.se2019.server.dataupdate;

import it.polimi.se2019.server.cards.weapons.Weapon;
import it.polimi.se2019.server.games.board.Board;
import it.polimi.se2019.util.LocalModel;

import java.util.List;

/**
 *  This update message is sent by the model to the views when one the weapon crates is re-spawned. Like all the
 *  other update messages the method updateData() must be run to update the local copy of the model in the view.
 *
 * @author Andrea Huang
 */
public class WeaponCrateUpdate implements StateUpdate {
    private int xPosition;
    private int yPosition;

    private List<Weapon> weaponCrate;

    /**
     * Builds a new WeaponCrateUpdate message. This message contains all the data that the view needs to update his
     * local model.
     *
     * @param xPosition position of the weapon crate on the board, x axis
     * @param yPosition position of the weapon crate on the board y axis
     * @param weaponCrate reference to the new weapon crate
     */
    public WeaponCrateUpdate(int xPosition, int yPosition, List<Weapon> weaponCrate) {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.weaponCrate = weaponCrate;
    }

    /**
     * This method must be run by the views (implements LocalModel) to update their local copy of the model.
     *
     * @param model view's local copy of the model that needs to be updated
     */
    @Override
    public void updateData(LocalModel model) {
        Board board = model.getBoard();

        board.setWeaponCrate(xPosition, yPosition, weaponCrate);
    }
}
