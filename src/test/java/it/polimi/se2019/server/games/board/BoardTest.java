package it.polimi.se2019.server.games.board;

import it.polimi.se2019.server.exceptions.TileNotFoundException;
import it.polimi.se2019.server.graphs.Graph;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.stream.IntStream;

public class BoardTest {

    Tile[][] tileMap;
    Board board;

    @Before
    public void setUp() {

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

        expectedPos[0] = 1;
        expectedPos[1] = 2;
        try {
            pos = board.getTilePosition(tileMap[1][2]);
        } catch(TileNotFoundException e) {
            Assert.fail("Tile not found.");
        }
        Assert.assertArrayEquals(expectedPos, pos);

    }

    @Test
    public void testGenerateGraph() {

        Graph<Tile> expectedGraph = new Graph<>();
        IntStream.range(0, tileMap[0].length)
                .forEach(y -> {
                    IntStream.range(0, tileMap.length)
                            .forEach(x -> {
                                //System.out.println(x + "," + y + ":" + tileMap[x][y]);
                                expectedGraph.addVertex(tileMap[x][y]);
                            });
                });
        expectedGraph.addEdge(tileMap[0][0], tileMap[1][0]);
        expectedGraph.addEdge(tileMap[0][0], tileMap[0][1]);
        expectedGraph.addEdge(tileMap[1][0], tileMap[1][1]);
        expectedGraph.addEdge(tileMap[0][1], tileMap[1][1]);
        expectedGraph.addEdge(tileMap[0][1], tileMap[0][2]);
        expectedGraph.addEdge(tileMap[0][2], tileMap[1][2]);

        Assert.assertEquals(true,
                board.generateGraph().getAdjacentVertices(tileMap[0][0]).containsAll(expectedGraph.getAdjacentVertices(tileMap[0][0])));
        Assert.assertEquals(true,
                board.generateGraph().getAdjacentVertices(tileMap[0][1]).containsAll(expectedGraph.getAdjacentVertices(tileMap[0][1])));
        Assert.assertEquals(true,
                board.generateGraph().getAdjacentVertices(tileMap[1][1]).containsAll(expectedGraph.getAdjacentVertices(tileMap[1][1])));
        Assert.assertEquals(true,
                board.generateGraph().getAdjacentVertices(tileMap[0][2]).containsAll(expectedGraph.getAdjacentVertices(tileMap[0][2])));
        Assert.assertEquals(true,
                board.generateGraph().getAdjacentVertices(tileMap[1][2]).containsAll(expectedGraph.getAdjacentVertices(tileMap[1][2])));

        Assert.assertEquals(false,
                board.generateGraph().getAdjacentVertices(tileMap[0][0]).containsAll(expectedGraph.getAdjacentVertices(tileMap[1][2])));
    }
}