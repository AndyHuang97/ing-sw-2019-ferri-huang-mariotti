package it.polimi.se2019.server.games.board;

import it.polimi.se2019.server.exceptions.TileNotFoundException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BoardTest {

    Tile[][] tileMap;
    Board board;

    @Before
    public void setUp() {
        board = new Board();
        tileMap = new Tile[2][2];
        LinkType[] links00 = {LinkType.WALL, LinkType.DOOR, LinkType.DOOR, LinkType.WALL};
        tileMap[0][0] = new NormalTile("Red", links00, null);
        LinkType[] links01 = {LinkType.WALL, LinkType.WALL, LinkType.OPEN, LinkType.DOOR};
        tileMap[0][1] = new NormalTile("Blue", links01, null);
        LinkType[] links10 = {LinkType.DOOR, LinkType.DOOR, LinkType.WALL, LinkType.WALL};
        tileMap[1][0] = new NormalTile("Yellow", links10, null);
        LinkType[] links11 = {LinkType.OPEN, LinkType.WALL, LinkType.WALL, LinkType.DOOR};
        tileMap[1][1] = new NormalTile("Blue", links01, null);
    }

    @After
    public void tearDown() {
        tileMap = null;
        board = null;
    }

    @Test
    public void testGetTilePostion() {

        int[] pos = null;
        int[] expectedPos = {0,0};
        board.setTileMap(tileMap);

        Assert.assertArrayEquals(tileMap[0], board.getTileMap()[0]);
        Assert.assertArrayEquals(tileMap[1], board.getTileMap()[1]);

        try {
            pos = board.getTilePosition(tileMap[0][0]);
        } catch(TileNotFoundException e) {
            Assert.fail("Tile not found.");
        }
        Assert.assertArrayEquals(expectedPos, pos);

    }

    @Test
    public void testGenerateGraph() {

    }
}