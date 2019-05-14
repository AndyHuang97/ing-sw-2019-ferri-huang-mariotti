package it.polimi.se2019.server.actions.effects;

import it.polimi.se2019.server.cards.weapons.Weapon;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class ReloadTargetTest {

    Weapon w;
    Game game;
    Map<String, List<Targetable>> targets;

    @Before
    public void setUp() {
        w = new Weapon(null, "Power Glove", null, null ,null);
        game = new Game();
        targets = new HashMap<>();
    }

    @After
    public void tearDown() {
        w = null;
    }
    @Test
    public void testReloadTarget() {
        Effect effect = new ReloadTarget();
        w.setLoaded(false);
        List<Targetable> weapon = new ArrayList<>();
        targets.put("weapon", weapon);

        weapon.add(w);
        effect.run(game, targets);
        assertTrue(w.isLoaded());
    }
}