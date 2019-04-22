package it.polimi.se2019.server.games.player;

import it.polimi.se2019.server.cards.ammo.Ammo;
import it.polimi.se2019.server.cards.ammo.AmmoColor;
import it.polimi.se2019.server.games.board.LinkType;
import it.polimi.se2019.server.games.board.Tile;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

public class CharacterStateTest {

    CharacterState characterState;

    @Before
    public void setUp() {
        characterState = new CharacterState();
    }

    @After
    public void tearDown() {
        characterState = null;
    }


    @Test
    public void testSetDamageBar() {
        PlayerColor dmg1 = PlayerColor.BLUE;
        PlayerColor dmg2 = PlayerColor.GREEN;
        ArrayList<PlayerColor> dmgBar = new ArrayList<>(Arrays.asList(dmg1, dmg2));

        characterState.setDamageBar(dmgBar);

        Assert.assertEquals(dmgBar, characterState.getDamageBar());
    }

    @Test
    public void testSetDeathCount() {
        Integer deaths = 3;
        characterState.setDeathCount(deaths);

        Assert.assertEquals(deaths, characterState.getDeathCount());
    }

    @Test
    public void testSetMarkerBar() {
        PlayerColor marker1 = PlayerColor.BLUE;
        PlayerColor marker2 = PlayerColor.PURPLE;
        ArrayList<PlayerColor> markerBar = new ArrayList<>(Arrays.asList(marker1, marker2));

        characterState.setMarkerBar(markerBar);

        Assert.assertEquals(markerBar, characterState.getMarkerBar());
    }

    @Test
    public void testSetAmmo() {
        Ammo ammo1 = new Ammo(AmmoColor.BLUE);
        Ammo ammo2 = new Ammo(AmmoColor.RED);
        ArrayList<Ammo> ammoList = new ArrayList<>(Arrays.asList(ammo1, ammo2));

        characterState.setAmmo(ammoList);

        Assert.assertEquals(ammoList, characterState.getAmmo());
    }

    @Test
    public void testSetTile() {
        Tile newTile = new Tile("RED", true, null, null, new LinkType[4]);

        characterState.setTile(newTile);

        Assert.assertEquals(newTile, characterState.getTile());
    }
}