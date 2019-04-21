package it.polimi.se2019.server.games;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

public class GameDataTest {

    GameData gameData;

    @Before
    public void setUp(){
        gameData = new GameData("abc", new Date());
    }

    @After
    public void tearDown(){
        gameData = null;
    }

    @Test
    public void serialize() {
    }

    @Test
    public void testSetId() {
        gameData.setId("def");

        Assert.assertEquals("def", gameData.getId());
    }

    @Test
    public void testSetStartDate() {
        Date newDate = new Date();
        gameData.setStartDate(newDate);

        Assert.assertEquals(newDate, gameData.getStartDate());
    }

}