package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.*;
import it.polimi.se2019.server.games.player.CharacterState;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.games.player.PlayerColor;
import it.polimi.se2019.server.users.UserData;
import it.polimi.se2019.util.CommandConstants;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

public class OneDirectionMovementTest {

    Tile tile;
    Tile[][] tileMap;
    Board board;
    Game game;
    Player p1, p2, p3, p4;
    Map<String, List<Targetable>> targets = new HashMap<>();
    List<Targetable> list;

    @Before
    public void setUp() {
        game = new Game();
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
        board = new Board("",tileMap);
        game.setBoard(board);


        p1 = new Player(UUID.randomUUID().toString(), true, new UserData("A"), new CharacterState(), PlayerColor.BLUE);
        p1.getCharacterState().setTile(tileMap[1][0]);
        p2 = new Player(UUID.randomUUID().toString(), true, new UserData("B"), new CharacterState(), PlayerColor.GREEN);
        p2.getCharacterState().setTile(tileMap[1][1]);
        p3 = new Player(UUID.randomUUID().toString(), true, new UserData("C"), new CharacterState(), PlayerColor.YELLOW);
        p3.getCharacterState().setTile(tileMap[1][2]);
        p4 = new Player(UUID.randomUUID().toString(), true, new UserData("D"), new CharacterState(), PlayerColor.GREY);
        p4.getCharacterState().setTile(tileMap[0][1]);
        game.setPlayerList(new ArrayList<>(Arrays.asList(p1,p2,p3,p4)));

        list = new ArrayList<>();

    }

    @After
    public void tearDown() {
        tile = null;
        tileMap = null;
        board = null;
        game = null;
        p1 = null;
        p2 = null;
        p3 =null;
        p4 = null;
        targets = null;
        list = null;
    }

    @Test
    public void testOneDirectionMovement() {
        Condition condition = new OneDirectionMovement(2);
        List<Targetable> tile = new ArrayList<>();
        targets.put(CommandConstants.TILELIST,tile);

        game.setCurrentPlayer(p1);
        tile.add(tileMap[1][2]);
        Assert.assertFalse(condition.check(game, targets));
        tile.clear();

        tile.add(tileMap[0][1]);
        Assert.assertFalse(condition.check(game, targets));
        tile.clear();

        tile.add(tileMap[1][1]);
        Assert.assertTrue(condition.check(game, targets));
        tile.clear();

        tile.add(tileMap[0][0]);
        Assert.assertTrue(condition.check(game, targets));
        tile.clear();
    }
}