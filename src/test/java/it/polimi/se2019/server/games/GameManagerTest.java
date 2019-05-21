package it.polimi.se2019.server.games;

import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.users.UserData;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Properties;

public class GameManagerTest {

    GameManager gameManager;

    @Before
    public void setUp() {
        gameManager = new GameManager();
    }

    @After
    public void tearDown() {
        gameManager = null;
    }

    @Test
    public void testRetrieveGame() {

    }


    @Test
    public void testDumpToFile() {
        gameManager.init("src/test/java/it/polimi/se2019/server/games/data/games_dump.json");
        List<Game> oldGames = gameManager.getGameList();
        gameManager.dumpToFile();
        gameManager.init("src/test/java/it/polimi/se2019/server/games/data/games_dump.json");
        Assert.assertEquals(oldGames, gameManager.getGameList());
    }

    @Test(expected = GameManager.AlreadyPlayingException.class)
    public void testWaitingList() throws GameManager.AlreadyPlayingException {
        UserData user = new UserData("testNick");
        gameManager.addUserToWaitingList(user);
        Assert.assertTrue(gameManager.getWaitingList().contains(user));
        gameManager.addUserToWaitingList(user);
    }

    @Test
    public void testGameStart() throws IOException, GameManager.AlreadyPlayingException {
        InputStream input = new FileInputStream("src/main/resources/config.properties");
        Properties prop = new Properties();
        // load a properties file
        prop.load(input);
        int waitingListMaxSize = Integer.parseInt(prop.getProperty("gamemanager.waitinglistmaxsize"));
        for (int i = 0; i <= waitingListMaxSize; i++) {
            UserData user = new UserData("testNick" + i);
            gameManager.addUserToWaitingList(user);
        }
        Assert.assertEquals(1, gameManager.getGameList().size());
    }

    @Test
    public void testGameRetrieve() throws IOException, GameManager.AlreadyPlayingException, GameManager.GameNotFoundException {
        InputStream input = new FileInputStream("src/main/resources/config.properties");
        Properties prop = new Properties();
        // load a properties file
        prop.load(input);
        int waitingListMaxSize = Integer.parseInt(prop.getProperty("gamemanager.waitinglistmaxsize"));
        for (int i = 0; i <= 2 * waitingListMaxSize; i++) {
            UserData user = new UserData("testNick" + i);
            gameManager.addUserToWaitingList(user);
        }
        Assert.assertNotEquals(gameManager.retrieveGame("testNick0"), gameManager.retrieveGame("testNick" + 2 * waitingListMaxSize));
    }

    @Test
    public void testGameCreate() {
        UserData user1 = new UserData("testNick1");
        UserData user2 = new UserData("testNick2");
        List<UserData> waitingList = Arrays.asList(user1, user2);
        Game game = gameManager.createGame(waitingList);
        Assert.assertTrue(game.getPlayerList().stream().anyMatch(player -> player.getUserData() == user1));
        Assert.assertTrue(game.getPlayerList().stream().anyMatch(player -> player.getUserData() == user2));
    }
}