package it.polimi.se2019.server.cards.ammo;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AmmoTest {
    Ammo ammo;

    @Before
    public void setUp() throws Exception {
        ammo = new Ammo(AmmoColor.RED);
    }

    @After
    public void tearDown() throws Exception {
        ammo = null;
    }

    @Test
    public void testConstruction() {
        Assert.assertEquals(AmmoColor.RED, ammo.getColor());
    }

    @Test
    public void testSetColor() {
        ammo.setColor(AmmoColor.BLUE);
        Assert.assertEquals(AmmoColor.BLUE, ammo.getColor());
    }
}