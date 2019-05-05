package it.polimi.se2019.server.actions.effects;

import it.polimi.se2019.server.cards.weapons.Weapon;

public class ReloadTarget implements Effect {

    private Weapon targetWeapon;

    public ReloadTarget(Weapon targetWeapon) {
        this.targetWeapon = targetWeapon;
    }

    @Override
    public void run() {

    }
}
