package it.polimi.se2019.server.games.board;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;


public class TileTest {

    Tile tile;

    @Before
    public void setUp() {
        tile = new NormalTile("RED", new LinkType[4], null);
    }

    @After
    public void tearDown() {
        tile = null;
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
}