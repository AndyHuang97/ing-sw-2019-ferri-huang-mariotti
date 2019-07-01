package it.polimi.se2019.server.dataupdate;

import it.polimi.se2019.server.cards.weapons.Weapon;
import it.polimi.se2019.util.LocalModel;

public class CurrentWeaponUnpdate implements StateUpdate {
    Weapon currentWeapon;

    public CurrentWeaponUnpdate(Weapon currentWeapon) {
        this.currentWeapon = currentWeapon;
    }

    @Override
    public void updateData(LocalModel model) {
        model.getGame().setCurrentWeapon(currentWeapon);
    }
}
