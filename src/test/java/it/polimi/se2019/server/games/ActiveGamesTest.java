package it.polimi.se2019.server.games;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

public class ActiveGamesTest {

    ActiveGames activeGames;

    @Before
    public void setUp() {
        activeGames = new ActiveGames();
    }

    @After
    public void tearDown() {
        activeGames = null;
    }

    @Test
    public void testAddGame() {

        ArrayList<Game> gameList = (ArrayList<Game>) activeGames.getGameList();
        Game game = new Game();
        int size = gameList.size();

        activeGames.addGame(game);

        Assert.assertEquals(size+1, gameList.size());
    }

    @Test
    public void testRetrieveGame() {
    }

    @Test
    public void testSetGameList() {
        Game game1 = new Game();
        Game game2 = new Game();
        ArrayList<Game> gameList = new ArrayList<>(Arrays.asList(game1, game2));

        activeGames.setGameList(gameList);

        Assert.assertEquals(gameList, activeGames.getGameList());
    }

}