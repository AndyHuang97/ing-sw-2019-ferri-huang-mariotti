package it.polimi.se2019.server.games.board;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.player.CharacterState;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.games.player.PlayerColor;
import it.polimi.se2019.server.users.UserData;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


public class TileTest {

    Tile tile;
    Tile[][] tileMap;
    Board board;
    Game game;
    Player p1, p2, p3, p4;

    @Before
    public void setUp() {
        game = new Game();
        tileMap = new Tile[2][2];
        LinkType[] links00 = {LinkType.WALL, LinkType.DOOR, LinkType.DOOR, LinkType.WALL};
        tileMap[0][0] = new Tile(RoomColor.RED, links00, null);
        LinkType[] links01 = {LinkType.DOOR, LinkType.DOOR, LinkType.WALL, LinkType.WALL};
        tileMap[0][1] = new Tile(RoomColor.YELLOW, links01, null);
        LinkType[] links10 = {LinkType.WALL, LinkType.WALL, LinkType.OPEN, LinkType.DOOR};
        tileMap[1][0] = new Tile(RoomColor.BLUE, links10, null);
        LinkType[] links11 = {LinkType.OPEN, LinkType.WALL, LinkType.WALL, LinkType.DOOR};
        tileMap[1][1] = new Tile(RoomColor.BLUE, links11, null);
        board = new Board("",tileMap);
        game.setBoard(board);


        p1 = new Player(UUID.randomUUID().toString(), true, new UserData("A"), new CharacterState(), PlayerColor.BLUE);
        p1.getCharacterState().setTile(tileMap[1][0]);
        p2 = new Player(UUID.randomUUID().toString(), true, new UserData("B"), new CharacterState(), PlayerColor.GREEN);
        p2.getCharacterState().setTile(tileMap[1][1]);
        p3 = new Player(UUID.randomUUID().toString(), true, new UserData("C"), new CharacterState(), PlayerColor.YELLOW);
        p3.getCharacterState().setTile(tileMap[1][0]);
        p4 = new Player(UUID.randomUUID().toString(), true, new UserData("D"), new CharacterState(), PlayerColor.GREY);
        p4.getCharacterState().setTile(tileMap[0][1]);
        game.setPlayerList(new ArrayList<>(Arrays.asList(p1,p2,p3,p4)));

        tile = tileMap[1][1];
    }

    @After
    public void tearDown() {
        tile = null;
        game = null;
        p1 = null;
        p2 = null;
        p3 = null;
        p4 = null;
        board = null;
    }

    /*
    @Test
    public void testSetColor() {

        tile.setRoomColor(RoomColor.BLUE);

        Assert.assertEquals(RoomColor.BLUE, tile.getRoomColor());
    }

    @Test
    public void testSetNorthLink() {

        tile.setNorthLink(LinkType.DOOR);

        Assert.assertEquals(LinkType.DOOR, tile.getNorthLink());
    }

    @Test
    public void testSetSouthLink() {

        tile.setSouthLink(LinkType.OPEN);

        Assert.assertEquals(LinkType.OPEN, tile.getSouthLink());
    }

    @Test
    public void testSetEastLink() {

        tile.setEastLink(LinkType.WALL);

        Assert.assertEquals(LinkType.WALL, tile.getEastLink());
    }

    @Test
    public void testSetWestLink() {

        tile.setWestLink(LinkType.WALL);

        Assert.assertEquals(LinkType.WALL, tile.getWestLink());
    }

    @Test
    public void testGetPlayers() {

        List<Player> playerList = new ArrayList<>(Arrays.asList(p2));

        Assert.assertEquals(playerList, tile.getPlayers(game));
    }
    */

    @Test
    public void testGetVisibleTiles() {

        List<Tile> visibleTiles = new ArrayList<>(Arrays.asList(tileMap[1][0], tileMap[1][1], tileMap[0][1]));
        tile = tileMap[1][1];
        Assert.assertEquals(visibleTiles, tile.getVisibleTiles(board));
    }

    @Test
    public void testGetVisibleTargets() {
        List<Player> expectedList = Arrays.asList(p1, p2, p3, p4);
        p4.getCharacterState().setTile(tileMap[1][1]);
        tile = tileMap[1][1];
        Assert.assertEquals(expectedList, tile.getVisibleTargets(game));
    }
}