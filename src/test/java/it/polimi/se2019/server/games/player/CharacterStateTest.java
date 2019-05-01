package it.polimi.se2019.server.games.player;

import it.polimi.se2019.server.cards.ammo.AmmoColor;
import it.polimi.se2019.server.games.PlayerDeath;
import it.polimi.se2019.server.games.board.LinkType;
import it.polimi.se2019.server.games.board.NormalTile;
import it.polimi.se2019.server.games.board.Tile;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

public class CharacterStateTest {

    CharacterState characterState;

    @Before
    public void setUp() {
        characterState = new CharacterState();
        characterState.getMarkerBar().put(PlayerColor.BLUE, 0);
        characterState.getMarkerBar().put(PlayerColor.GREEN, 0);
        characterState.getMarkerBar().put(PlayerColor.YELLOW, 0);
        characterState.getMarkerBar().put(PlayerColor.GREY, 0);
        characterState.getMarkerBar().put(PlayerColor.PURPLE, 0);
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
    public void testSetMarkerBar() {
        PlayerColor marker1 = PlayerColor.BLUE;
        PlayerColor marker2 = PlayerColor.PURPLE;
        EnumMap<PlayerColor, Integer> markerBar = new EnumMap<>(PlayerColor.class);

        characterState.setMarkerBar(markerBar);

        Assert.assertEquals(markerBar, characterState.getMarkerBar());
    }

    @Test
    public void testSetAmmo() {
        EnumMap<AmmoColor, Integer> ammoList = new EnumMap<>(AmmoColor.class);

        characterState.setAmmoBag(ammoList);

        Assert.assertEquals(ammoList, characterState.getAmmoBag());
    }

    @Test
    public void testSetTile() {
        Tile newTile = new NormalTile("RED", new LinkType[4], null);

        characterState.setTile(newTile);

        Assert.assertEquals(newTile, characterState.getTile());
    }

    @Test
    public void testAddDamage() {
        int oldSize = characterState.getDamageBar().size();

        characterState.addDamage(PlayerColor.BLUE, 2);

        Assert.assertEquals(oldSize+2, characterState.getDamageBar().size());
        Assert.assertEquals(PlayerColor.BLUE, characterState.getDamageBar().get(oldSize));
        Assert.assertEquals(PlayerColor.BLUE, characterState.getDamageBar().get(oldSize+1));
    }

    @Test
    public void testResetDamageBar() {
        characterState.resetDamageBar();

        Assert.assertEquals(0, characterState.getDamageBar().size());
    }

    @Test
    public void testSetCharacterValue() {
        characterState.setCharacterValue(CharacterValue.ONEDEATH);
        
        Assert.assertEquals(CharacterValue.ONEDEATH, characterState.getCharacterValue());
    }

    @Test
    public void testAddMarker() {
        int oldSize = characterState.getMarkerBar().size();

        characterState.addMarker(PlayerColor.YELLOW, 2);

        Assert.assertEquals(2, characterState.getMarkerBar().get(PlayerColor.YELLOW).intValue());
    }

    @Test
    public void testResetMarkerBar() {
        characterState.resetMarkerBar();

        Assert.assertEquals(0, characterState.getMarkerBar().size());
    }

    @Test
    public void testSetScore() {
        characterState.setScore(100);

        Assert.assertEquals(100, characterState.getScore().intValue());
    }

    @Test
    public void testUpdateScore() {
        PlayerColor p1, p2, p3;
        p1 = PlayerColor.GREEN;
        p2 = PlayerColor.PURPLE;
        p3 = PlayerColor.YELLOW;
        List<PlayerColor> damageBar = new ArrayList<>(Arrays.asList(p3,p3,p3,p2,p2,p3,p3,p2,p2,p3,p1));
        PlayerDeath message = new PlayerDeath(PlayerColor.BLUE, damageBar, PlayerColor.YELLOW, CharacterValue.ZERODEATHS);

        characterState.setScore(0);
        characterState.updateScore(message, PlayerColor.YELLOW);
        Assert.assertEquals(9, characterState.getScore().intValue());

        characterState.setScore(0);
        characterState.updateScore(message, PlayerColor.PURPLE);
        Assert.assertEquals(6, characterState.getScore().intValue());

        characterState.setScore(0);
        characterState.updateScore(message, PlayerColor.GREEN);
        Assert.assertEquals(4, characterState.getScore().intValue());
    }
}