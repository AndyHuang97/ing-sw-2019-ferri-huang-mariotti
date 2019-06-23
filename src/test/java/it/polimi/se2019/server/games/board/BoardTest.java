package it.polimi.se2019.server.games.board;

import it.polimi.se2019.server.actions.Direction;
import it.polimi.se2019.server.exceptions.TileNotFoundException;
import it.polimi.se2019.server.graphs.Graph;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class BoardTest {

    Tile[][] tileMap;
    Board board;

    @Before
    public void setUp() {

        tileMap = new Tile[2][4];
        LinkType[] links00 = {LinkType.WALL, LinkType.DOOR, LinkType.DOOR, LinkType.WALL};
        tileMap[0][0] = new Tile(RoomColor.RED, links00, null);
        LinkType[] links01 = {LinkType.DOOR, LinkType.DOOR, LinkType.OPEN, LinkType.WALL};
        tileMap[0][1] = new Tile(RoomColor.YELLOW, links01, null);
        LinkType[] links10 = {LinkType.WALL, LinkType.WALL, LinkType.OPEN, LinkType.DOOR};
        tileMap[1][0] = new Tile(RoomColor.BLUE, links10, null);
        LinkType[] links11 = {LinkType.OPEN, LinkType.WALL, LinkType.WALL, LinkType.DOOR};
        tileMap[1][1] = new Tile(RoomColor.BLUE, links11, null);
        LinkType[] links02 = {LinkType.OPEN, LinkType.DOOR, LinkType.OPEN, LinkType.WALL};
        tileMap[0][2] = new Tile(RoomColor.YELLOW, links02, null);
        LinkType[] links12 = {LinkType.WALL, LinkType.WALL, LinkType.WALL, LinkType.DOOR};
        tileMap[1][2] = new Tile(RoomColor.WHITE, links12, null);
        LinkType[] links03 = {LinkType.OPEN, LinkType.OPEN, LinkType.WALL, LinkType.WALL};
        tileMap[0][3] = new Tile(RoomColor.YELLOW, links03, null);
        LinkType[] links13 = {LinkType.WALL, LinkType.WALL, LinkType.WALL, LinkType.OPEN};
        board = new Board("",tileMap);
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

        assertArrayEquals(tileMap[0], board.getTileMap()[0]);
        assertArrayEquals(tileMap[1], board.getTileMap()[1]);

        try {
            pos = board.getTilePosition(tileMap[0][0]);
        } catch(TileNotFoundException e) {
            fail("Tile not found.");
        }
        assertArrayEquals(expectedPos, pos);

        expectedPos[0] = 1;
        expectedPos[1] = 2;
        try {
            pos = board.getTilePosition(tileMap[1][2]);
        } catch(TileNotFoundException e) {
            fail("Tile not found.");
        }
        assertArrayEquals(expectedPos, pos);

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

        assertEquals(true,
                board.generateGraph().getAdjacentVertices(tileMap[0][0]).containsAll(expectedGraph.getAdjacentVertices(tileMap[0][0])));
        assertEquals(true,
                board.generateGraph().getAdjacentVertices(tileMap[0][1]).containsAll(expectedGraph.getAdjacentVertices(tileMap[0][1])));
        assertEquals(true,
                board.generateGraph().getAdjacentVertices(tileMap[1][1]).containsAll(expectedGraph.getAdjacentVertices(tileMap[1][1])));
        assertEquals(true,
                board.generateGraph().getAdjacentVertices(tileMap[0][2]).containsAll(expectedGraph.getAdjacentVertices(tileMap[0][2])));
        assertEquals(true,
                board.generateGraph().getAdjacentVertices(tileMap[1][2]).containsAll(expectedGraph.getAdjacentVertices(tileMap[1][2])));

        assertEquals(false,
                board.generateGraph().getAdjacentVertices(tileMap[0][0]).containsAll(expectedGraph.getAdjacentVertices(tileMap[1][2])));
    }

    @Test
    public void testGetDirection() {
        assertEquals(Direction.NORTH, board.getDirection(tileMap[0][1],tileMap[0][0]));
        assertEquals(Direction.EAST, board.getDirection(tileMap[0][0],tileMap[1][0]));
        assertEquals(Direction.SOUTH, board.getDirection(tileMap[0][0],tileMap[0][1]));
        assertEquals(Direction.WEST, board.getDirection(tileMap[1][0],tileMap[0][0]));
    }

    @Test
    public void testIsOneDirectionList() {
        List<Tile> tileList = new ArrayList<>();
        tileList.add(tileMap[0][1]);
        tileList.add(tileMap[0][2]);
        tileList.add(tileMap[0][3]);
        assertTrue(board.isOneDirectionList(Direction.SOUTH,tileMap[0][0],tileList));

        tileList.clear();
        tileList.add(tileMap[0][1]);
        tileList.add(tileMap[1][1]);
        tileList.add(tileMap[1][0]);
        assertFalse(board.isOneDirectionList(Direction.SOUTH,tileMap[0][0],tileList));

        tileList.clear();
        tileList.add(tileMap[1][1]);
        assertFalse(board.isOneDirectionList(Direction.SOUTH,tileMap[0][0],tileList));

    }

    @Test
    public void testGetTilesAtDistance() {
        List<Tile> tileList = new ArrayList<>();
        tileList.add(tileMap[1][0]);
        tileList.add(tileMap[0][1]);
        assertTrue(tileList.containsAll(board.getTilesAtDistance(tileMap[0][0], 1)));
        tileList.clear();
        tileList.add(tileMap[1][1]);
        tileList.add(tileMap[0][2]);
        assertTrue(tileList.containsAll(board.getTilesAtDistance(tileMap[0][0], 2)));
        tileList.clear();
        tileList.add(tileMap[1][2]);
        tileList.add(tileMap[0][3]);
        assertTrue(tileList.containsAll(board.getTilesAtDistance(tileMap[0][0], 3)));
        tileList.clear();
        tileList.add(tileMap[1][3]);
        assertTrue(tileList.containsAll(board.getTilesAtDistance(tileMap[0][0], 4)));
        tileList.clear();
        assertTrue(board.getTilesAtDistance(tileMap[0][0], 5).isEmpty());
        assertTrue(board.getTilesAtDistance(tileMap[0][0], 0).contains(tileMap[0][0]));
    }
}