package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.games.player.PlayerColor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

import java.util.Arrays;
import java.util.List;

public class DifferentTargetsInListTest {

    Player player1;
    Player player2;

    @Before
    public void setUp() throws Exception {
        player1 = new Player(false, null, null, PlayerColor.BLUE);
        player2 = new Player(false, null, null, PlayerColor.GREEN);
    }

    @After
    public void tearDown() throws Exception {
        player1 = null;
        player2 = null;
    }

    @Test
    public void testDifferentTargets() {
        List<Player> testList1 = Arrays.asList(player1, player2);
        //DifferentTargetsInList testCheck1 = new DifferentTargetsInList(testList1);
        //Assert.assertEquals(true, testCheck1.check());
        List<Player> testList2 = Arrays.asList(player1, player1);
        //DifferentTargetsInList testCheck2 = new DifferentTargetsInList(testList2);
        //Assert.assertEquals(false, testCheck2.check());
    }
}