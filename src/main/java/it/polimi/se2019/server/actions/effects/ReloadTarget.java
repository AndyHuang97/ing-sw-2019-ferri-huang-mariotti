package it.polimi.se2019.server.actions.effects;

import it.polimi.se2019.server.cards.weapons.Weapon;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;

import java.util.List;
import java.util.Map;

public class ReloadTarget implements Effect {

    @Override
    public void run(Game game, Map<String, List<Targetable>> targets) {
        Weapon weapon = (Weapon) targets.get("weapon").get(0);

        weapon.setLoaded(true);
    }
}
