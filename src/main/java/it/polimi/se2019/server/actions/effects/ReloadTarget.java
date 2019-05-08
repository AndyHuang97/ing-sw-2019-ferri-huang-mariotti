package it.polimi.se2019.server.actions.effects;

import it.polimi.se2019.server.cards.weapons.Weapon;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;

import java.util.Map;

public class ReloadTarget implements Effect {

    private Weapon targetWeapon;

    public ReloadTarget(Weapon targetWeapon) {
        this.targetWeapon = targetWeapon;
    }

    @Override
    public void run(Game game, Map<String, Map<Targetable, Integer>> targets) {

    }
}
