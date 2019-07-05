package it.polimi.se2019.server.games;

import it.polimi.se2019.server.exceptions.PlayerNotFoundException;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.net.CommandHandler;
import it.polimi.se2019.server.net.socket.SocketServer;
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
import java.util.logging.Logger;

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
        // TODO: make test useful
        gameManager.init("src/test/java/it/polimi/se2019/server/games/data/games_dump.json");
        Assert.assertEquals(oldGames, gameManager.getGameList());
    }

    @Test(expected = GameManager.AlreadyPlayingException.class)
    public void testWaitingList() throws GameManager.AlreadyPlayingException {
        gameManager.init("src/test/java/it/polimi/se2019/server/games/data/games_dump.json");
        UserData user = new UserData("testNick");
        gameManager.addUserToWaitingList(user, new CommandHandler(), false);
        Assert.assertTrue(gameManager.getWaitingList().stream().anyMatch(tuple -> tuple.userData == user));
        gameManager.addUserToWaitingList(user, new CommandHandler(), false);
    }

    @Test()
    public void testWaitingListDisconnection() throws GameManager.AlreadyPlayingException, InterruptedException {
        gameManager.init("src/test/java/it/polimi/se2019/server/games/data/games_dump.json");
        UserData user = new UserData("testNick");
        gameManager.addUserToWaitingList(user, new CommandHandler(), true);
        Thread.sleep(2000);
        Assert.assertTrue(gameManager.getWaitingList().stream().anyMatch(tuple -> tuple.userData == user));
    }

    @Test
    public void testGameStart() throws IOException, GameManager.AlreadyPlayingException {
        gameManager.init("src/test/java/it/polimi/se2019/server/games/data/games_dump.json");
        InputStream input = new FileInputStream("src/main/resources/config.properties");
        Properties prop = new Properties();
        // load a properties file
        prop.load(input);
        int waitingListMaxSize = Integer.parseInt(prop.getProperty("game_manager.waiting_list_max_size"));
        System.out.println(waitingListMaxSize);
        for (int i = 0; i <= waitingListMaxSize; i++) {
            UserData user = new UserData("testNick" + i);
            gameManager.getMapPreference().add("0");
            gameManager.addUserToWaitingList(user, new CommandHandler(), false);
        }
        Logger.getGlobal().info(gameManager.getGameList().toString());
        Assert.assertEquals(1, gameManager.getGameList().size());
        gameManager.getGameList().clear();
    }

    @Test
    public void testGameRetrieve() throws IOException, GameManager.AlreadyPlayingException, GameManager.GameNotFoundException {
        InputStream input = new FileInputStream("src/main/resources/config.properties");
        Properties prop = new Properties();
        // load a properties file
        prop.load(input);
        int waitingListMaxSize = Integer.parseInt(prop.getProperty("game_manager.waiting_list_max_size"));
        for (int i = 0; i <= 2 * waitingListMaxSize; i++) {
            UserData user = new UserData("testNick" + i);
            gameManager.getMapPreference().add("0");
            gameManager.addUserToWaitingList(user, new CommandHandler());
        }
        Assert.assertNotEquals(gameManager.retrieveGame("testNick0"), gameManager.retrieveGame("testNick" + 2 * waitingListMaxSize));
        gameManager.getGameList().clear();
    }
}