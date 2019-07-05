package it.polimi.se2019.server.dataupdate;

import it.polimi.se2019.server.cards.weapons.Weapon;
import it.polimi.se2019.util.LocalModel;

/**
 * This update message is sent by the model to the views when the value of currentWeapon is updated in game.
 * Like all the other update messages the method updateData() must be run to update the local copy of the model
 * in the view.
 *
 * @author Rodolfo Mariotti
 */
public class WeaponStateUpdate implements StateUpdate {
    private Weapon weaponToUpdate;

    /**
     * Builds a new WeaponStateUpdate message. This message contains all the data that the view needs to update his
     * local model.
     *
     * @param weaponToUpdate reference to the updated weapon object
     */
    public WeaponStateUpdate(Weapon weaponToUpdate) {
        this.weaponToUpdate = weaponToUpdate;
    }

    /**
     * This method must be run by the views (implements LocalModel) to update their local copy of the model.
     *
     * @param model view's local copy of the model that needs to be updated
     */
    @Override
    public void updateData(LocalModel model) {
        model.updatePlayerWeapon(weaponToUpdate);
    }
}
