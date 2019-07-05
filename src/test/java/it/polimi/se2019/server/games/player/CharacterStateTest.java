package it.polimi.se2019.server.games.player;

import it.polimi.se2019.server.actions.conditions.Condition;
import it.polimi.se2019.server.actions.conditions.HasAmmo;
import it.polimi.se2019.server.cards.powerup.PowerUp;
import it.polimi.se2019.server.deserialize.DirectDeserializers;
import it.polimi.se2019.server.games.Deck;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.PlayerDeath;
import it.polimi.se2019.server.games.board.LinkType;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.board.RoomColor;
import it.polimi.se2019.server.users.UserData;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

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
    public void testInitAmmoBag() {
        characterState.setAmmoBag(characterState.initAmmoBag());

        Assert.assertTrue(characterState.getAmmoBag().keySet().containsAll(Arrays.asList(AmmoColor.values())));

    }

    @Test
    public void testInitMarkerBar() {
        characterState.setMarkerBar(characterState.initMarkerBar());

        Assert.assertTrue(characterState.getMarkerBar().keySet().containsAll(Arrays.asList(PlayerColor.values())));

    }
    @Test
    public void testSetTile() {
        Tile newTile = new Tile(RoomColor.RED, new LinkType[4], null);

        characterState.setTile(newTile);

        Assert.assertEquals(newTile, characterState.getTile());
    }

    @Test
    public void testAddDamage() {
        int oldSize = characterState.getDamageBar().size();
        Game game = new Game();
        characterState.addDamage(PlayerColor.BLUE, 2, game);

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
    public void testAddMarker() {
        int oldSize = characterState.getMarkerBar().size();

        characterState.addMarker(PlayerColor.YELLOW, 2);

        Assert.assertEquals(2, characterState.getMarkerBar().get(PlayerColor.YELLOW).intValue());
    }

    @Test
    public void testResetMarkerBar() {
        characterState.resetMarkerBar();

        characterState.getMarkerBar().keySet()
                .forEach(k -> Assert.assertEquals(0, characterState.getMarkerBar().get(k).intValue()));
        Assert.assertTrue(characterState.getMarkerBar().keySet().containsAll(Arrays.asList(PlayerColor.values())));
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

        Player player = new Player("", true, new UserData("Jon Snow"), new CharacterState(), PlayerColor.BLUE);
        player.getCharacterState().setDamageBar(damageBar);
        player.getCharacterState().setDeaths(0);
        PlayerDeath message = new PlayerDeath(player, false);

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

    @Test
    public void testPowerUpAsAmmo() {
        new DirectDeserializers();
        Deck<PowerUp> powerUpDeck = DirectDeserializers.deserialzePowerUpDeck();
        characterState.addPowerUp(powerUpDeck.drawCard());
        characterState.addPowerUp(powerUpDeck.drawCard());
        characterState.addPowerUp(powerUpDeck.drawCard());

        AmmoColor color0 = characterState.getPowerUpBag().get(0).getPowerUpColor();
        System.out.println(color0);
        AmmoColor color1 = characterState.getPowerUpBag().get(1).getPowerUpColor();
        System.out.println(color1);
        AmmoColor color2 = characterState.getPowerUpBag().get(2).getPowerUpColor();
        System.out.println(color2);

        Map<AmmoColor, Integer> neededAmmo = new HashMap<>();

        neededAmmo.put(AmmoColor.BLUE, 0);
        neededAmmo.put(AmmoColor.RED, 0);
        neededAmmo.put(AmmoColor.YELLOW, 0);
        neededAmmo.put(color0, neededAmmo.get(color0) + 1);
        neededAmmo.put(color1, neededAmmo.get(color1) + 1);
        neededAmmo.put(color2, neededAmmo.get(color2) + 1);

        System.out.println("needed color 2 " + neededAmmo.get(color2));

        System.out.println("needed " + neededAmmo.get(color0));

        Condition hasAmmo = new HasAmmo(neededAmmo);

        Game game = new Game();
        Player player = new Player("TESTNICK", true, null, characterState, PlayerColor.BLUE);
        game.setPlayerList(Arrays.asList(player));
        game.setCurrentPlayer(player);

        Assert.assertEquals(3, characterState.getPowerUpBag().size());
        Assert.assertTrue(hasAmmo.check(game, null));

        characterState.consumeAmmo(neededAmmo, game);

        Assert.assertEquals(0, characterState.getPowerUpBag().size());
    }
}