package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.actions.ActionUnit;
import it.polimi.se2019.server.games.Game;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class ExecutedActionUnitsTest {

    Game game;

    @Before
    public void setUp() throws Exception {
        game = new Game();

    }

    @After
    public void tearDown() throws Exception {
        game = null;
    }

    @Test
    public void testExecutedActionUnits() {
        Condition condition;
        ActionUnit basicMode = new ActionUnit(true,"Basic Mode", null, null, 0,0,true);
        game.setCurrentActionUnitsList(Arrays.asList(basicMode));

        condition = new ExecutedActionUnits(Arrays.asList("Basic Mode"));
        assertTrue(condition.check(game, null));
        condition = new ExecutedActionUnits(Arrays.asList("Basic Mode","Chain Reaction Effect"));
        assertFalse(condition.check(game, null));

    }
}