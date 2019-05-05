package it.polimi.se2019.server.games.board;

import it.polimi.se2019.server.exceptions.TileNotFoundException;
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


public class TileTest {

    Tile tile;
    Tile[][] tileMap;
    Board board;
    Game game;
    Player p1, p2;

    @Before
    public void setUp() {
        game = new Game();
        tileMap = new Tile[2][2];
        LinkType[] links00 = {LinkType.WALL, LinkType.DOOR, LinkType.DOOR, LinkType.WALL};
        tileMap[0][0] = new NormalTile("Red", links00, null);
        LinkType[] links01 = {LinkType.DOOR, LinkType.DOOR, LinkType.WALL, LinkType.WALL};
        tileMap[0][1] = new NormalTile("Yellow", links01, null);
        LinkType[] links10 = {LinkType.WALL, LinkType.WALL, LinkType.OPEN, LinkType.DOOR};
        tileMap[1][0] = new NormalTile("Blue", links10, null);
        LinkType[] links11 = {LinkType.OPEN, LinkType.WALL, LinkType.WALL, LinkType.DOOR};
        tileMap[1][1] = new NormalTile("Blue", links11, null);
        board = new Board(tileMap);
        game.setBoard(board);

        tile = tileMap[1][1];
        p1 = new Player(true, new UserData("A"), new CharacterState(), PlayerColor.BLUE);
        p1.getCharacterState().setTile(tile);
        p2 = new Player(true, new UserData("B"), new CharacterState(), PlayerColor.GREEN);
        p2.getCharacterState().setTile(tile);
        game.setPlayerList(new ArrayList<>(Arrays.asList(p1,p2)));



    }

    @After
    public void tearDown() {
        tile = null;
        game = null;
        p1 = null;
        p2 = null;
    }

    @Test
    public void testSetColor() {

        tile.setColor("BLUE");

        Assert.assertEquals("BLUE", tile.getColor());
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

        List<Player> playerList = new ArrayList<>(Arrays.asList(p1,p2));

        Assert.assertEquals(playerList, tile.getPlayers(game));
    }

    @Test
    public void testGetVisibleTiles() {

        List<Tile> visibleTiles = new ArrayList<>(Arrays.asList(tileMap[1][0], tileMap[1][1], tileMap[0][1]));

        Assert.assertEquals(visibleTiles, tile.getVisibleTiles(board));
    }
}