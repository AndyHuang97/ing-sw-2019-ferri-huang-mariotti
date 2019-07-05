package it.polimi.se2019.server.dataupdate;

import it.polimi.se2019.server.cards.weapons.Weapon;
import it.polimi.se2019.util.LocalModel;

public class WeaponStateUpdate implements StateUpdate {
    Weapon weaponToUpdate;

    public WeaponStateUpdate(Weapon weaponToUpdate) {
        this.weaponToUpdate = weaponToUpdate;
    }

    @Override
    public void updateData(LocalModel model) {
        model.updatePlayerWeapon(weaponToUpdate);
    }
}
