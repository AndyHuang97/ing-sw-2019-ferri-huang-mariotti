package it.polimi.se2019.server.controller;

import it.polimi.se2019.server.exceptions.PlayerNotFoundException;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.GameManager;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.*;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.net.CommandHandler;
import it.polimi.se2019.server.users.UserData;
import it.polimi.se2019.util.Message;
import it.polimi.se2019.util.Request;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ControllerTest {
    private GameManager gameManager;
    private Controller controller;
    private Tile[][] tileMap;
    private Board board;
    private CommandHandler actualPlayerCommandHandler;
    static final String testNick = "testNick0";


    @Before
    public void setUp() throws GameManager.AlreadyPlayingException, GameManager.GameNotFoundException, PlayerNotFoundException {
        gameManager = new GameManager();
        // GameManager and Game init
        gameManager.init("src/test/java/it/polimi/se2019/server/games/data/games_dump.json");

        int waitingListMaxSize = 5;
        for (int i = 0; i <= waitingListMaxSize; i++) {
            UserData user = new UserData("testNick" + i);
            if (i == 0) {
                actualPlayerCommandHandler = new CommandHandler();
                gameManager.addUserToWaitingList(user, actualPlayerCommandHandler);
            }
            else {
                gameManager.addUserToWaitingList(user, new CommandHandler());
            }
        }

        Game game = gameManager.retrieveGame(testNick);

        // Board init
        tileMap = new Tile[2][3];
        LinkType[] links00 = {LinkType.WALL, LinkType.DOOR, LinkType.DOOR, LinkType.WALL};
        tileMap[0][0] = new NormalTile(RoomColor.RED, links00, null);
        LinkType[] links01 = {LinkType.DOOR, LinkType.DOOR, LinkType.OPEN, LinkType.WALL};
        tileMap[0][1] = new NormalTile(RoomColor.YELLOW, links01, null);
        LinkType[] links10 = {LinkType.WALL, LinkType.WALL, LinkType.OPEN, LinkType.DOOR};
        tileMap[1][0] = new NormalTile(RoomColor.BLUE, links10, null);
        LinkType[] links11 = {LinkType.OPEN, LinkType.WALL, LinkType.WALL, LinkType.DOOR};
        tileMap[1][1] = new NormalTile(RoomColor.BLUE, links11, null);
        LinkType[] links02 = {LinkType.OPEN, LinkType.DOOR, LinkType.WALL, LinkType.WALL};
        tileMap[0][2] = new NormalTile(RoomColor.YELLOW, links02, null);
        LinkType[] links12 = {LinkType.WALL, LinkType.WALL, LinkType.WALL, LinkType.DOOR};
        tileMap[1][2] = new NormalTile(RoomColor.WHITE, links12, null);
        board = new Board(tileMap);

        game.setBoard(board);

        // Spawn players
        Player player0 = game.getPlayerByNickname(testNick);
        player0.getCharacterState().setTile(tileMap[0][0]);

        // Set testNick0 as current player
        game.setCurrentPlayer(player0);

        // Controller init
        controller = new Controller(gameManager);
    }

    @Test
    public void test() throws GameManager.GameNotFoundException, PlayerNotFoundException {
        Player player = gameManager.retrieveGame(testNick).getPlayerByNickname(testNick);

        List<Targetable> targetableList = new ArrayList<>();
        targetableList.add(tileMap[0][1]);

        Map<String, List<Targetable>> command = new HashMap<>();
        command.put("MovePlayerAction", targetableList);

        Message message =  new Message(command);
        Request request = new Request(message, testNick);

        actualPlayerCommandHandler.handle(request);

        Assert.assertSame(player.getCharacterState().getTile(), tileMap[0][1]);
    }
}
