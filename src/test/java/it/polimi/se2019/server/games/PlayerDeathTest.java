package it.polimi.se2019.server.games;

import it.polimi.se2019.server.games.player.CharacterValue;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.games.player.PlayerColor;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlayerDeathTest {

    PlayerDeath pd;
    List<PlayerColor> damageBar;
    PlayerColor p1, p2, p3;

    @Before
    public void setUp() {
        p1 = PlayerColor.GREEN;
        p2 = PlayerColor.PURPLE;
        p3 = PlayerColor.YELLOW;
        damageBar = new ArrayList<>(Arrays.asList(p3,p3,p3,p2,p2,p3,p3,p2,p2,p3,p1));
        pd = new PlayerDeath(PlayerColor.BLUE, damageBar, PlayerColor.YELLOW, CharacterValue.ZERODEATHS);

    }

    @After
    public void tearDown() {
        p1 = null;
        p2 = null;
        p3 = null;
        pd = null;
        damageBar = null;
    }

    @Test
    public void testRankAttackers() {

        List<PlayerColor> expectedRanking = new ArrayList<>(Arrays.asList(p3,p2,p1));

        List<PlayerColor> rankedAttackers = pd.rankAttackers(damageBar);

        Assert.assertEquals(expectedRanking, rankedAttackers);
    }

    @Test
    public void testSetAttackers() {
        List<PlayerColor> expectedRanking = new ArrayList<>(Arrays.asList(p1,p3,p2));

        pd.setAttackers(expectedRanking);

        Assert.assertEquals(expectedRanking, pd.getAttackers());
    }

    @Test
    public void testSetFirstAttacker() {
        pd.setFirstAttacker(PlayerColor.YELLOW);

        Assert.assertEquals(PlayerColor.YELLOW, pd.getFirstAttacker());
    }

    @Test
    public void testSetCharacterValue() {
        pd.setCharacterValue(CharacterValue.FOURDEATHS);

        Assert.assertEquals(CharacterValue.FOURDEATHS, pd.getCharacterValue());
    }

    @Test
    public void testSetDeadPlayer() {
        pd.setDeadPlayer(PlayerColor.PURPLE);

        Assert.assertEquals(PlayerColor.PURPLE, pd.getDeadPlayer());
    }
}