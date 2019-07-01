package it.polimi.se2019.server.cards.weapons;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.polimi.se2019.server.actions.ActionUnit;
import it.polimi.se2019.server.deserialize.*;
import it.polimi.se2019.server.games.Deck;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.*;
import it.polimi.se2019.server.games.player.AmmoColor;
import it.polimi.se2019.server.games.player.CharacterState;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.games.player.PlayerColor;
import it.polimi.se2019.server.users.UserData;
import it.polimi.se2019.util.CommandConstants;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class AllWeaponTest {

    Tile tile;
    Tile[][] tileMap;
    Board board;
    Game game;
    Player p1, p2, p3, p4, p5;

    DynamicDeserializerFactory factory;
    List<Weapon> weaponList;

    @Before
    public void setUp() {

        factory = new DynamicDeserializerFactory();
        factory.registerDeserializer("weapondeck", new WeaponDeckDeserializerSuppier());
        factory.registerDeserializer("weapon", new WeaponDeserializerSupplier());
        factory.registerDeserializer("actions", new ActionsDeserializerSupplier());
        factory.registerDeserializer("optionaleffects", new OptionalEffectDeserializerSupplier());
        factory.registerDeserializer("actionunit", new ActionUnitDeserializerSupplier());
        factory.registerDeserializer("effects", new EffectDeserializerSupplier());
        factory.registerDeserializer("conditions", new ConditionDeserializerSupplier());

        WeaponDeckDeserializer weaponDeckDeserializer = (WeaponDeckDeserializer) factory.getDeserializer("weapondeck");
        String weaponPath = "src/main/resources/json/weapons/weapons.json";
        Deck<Weapon> weaponDeck = null;

        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(weaponPath))) {

            JsonParser parser = new JsonParser();
            JsonObject wjson = parser.parse(bufferedReader).getAsJsonObject();

            weaponDeck = weaponDeckDeserializer.deserialize(wjson, factory);

        } catch (IOException | ClassNotFoundException e) {
            Logger.getGlobal().warning(e.toString());
        }

        weaponList = new ArrayList<>();
        Weapon weapon;
        while((weapon = weaponDeck.drawCard()) != null) {
            weaponList.add(weapon);
        }

        game = new Game();
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
        tileMap[1][3] = new Tile(RoomColor.YELLOW, links13, null);
        board = new Board("",tileMap);
        game.setBoard(board);


        p1 = new Player("P1", true, new UserData("P1"), new CharacterState(), PlayerColor.BLUE);
        p2 = new Player("P2", true, new UserData("P2"), new CharacterState(), PlayerColor.GREEN);
        p3 = new Player("P3", true, new UserData("P3"), new CharacterState(), PlayerColor.YELLOW);
        p4 = new Player("P4", true, new UserData("P4"), new CharacterState(), PlayerColor.GREY);
        p5 = new Player("P5", true, new UserData("P5"), new CharacterState(), PlayerColor.PURPLE);
        game.setPlayerList(new ArrayList<>(Arrays.asList(p1,p2,p3,p4,p5)));
    }

    @After
    public void tearDown() {
        tile = null;
        tileMap = null;
        board = null;
        game = null;
        p1 = null;
        p2 = null;
        p3 =null;
        p4 = null;
        p5 = null;
    }

    @Test
    public void testLockRifle() {
        Weapon weapon = weaponList.stream().filter(w -> w.getName().equals("Lock_Rifle")).findFirst().orElse(null);
        System.out.println(weapon.getName());

        game.setCurrentPlayer(p1); // the attacker
        p1.getCharacterState().setTile(tileMap[0][0]);
        p1.getCharacterState().addWeapon(weapon);
        p2.getCharacterState().resetDamageBar();
        p2.getCharacterState().resetMarkerBar();
        p3.getCharacterState().resetDamageBar();
        p3.getCharacterState().resetMarkerBar();
        p4.getCharacterState().resetDamageBar();
        p4.getCharacterState().resetMarkerBar();
        p5.getCharacterState().resetDamageBar();
        p5.getCharacterState().resetMarkerBar();

        // testing the availability of the action units
        assertTrue(weapon.isLoaded());
        assertTrue(weapon.getActionUnitList().stream()
                .map(ActionUnit::getName)
                .collect(Collectors.toList()).containsAll(Arrays.asList("Basic Mode"))
                );
        assertTrue(weapon.getOptionalEffectList().stream()
                .map(ActionUnit::getName)
                .collect(Collectors.toList()).containsAll(Arrays.asList("Second Lock Effect"))
        );

        // testing Basic Mode --------------------------------------------------------------------
        System.out.println("Basic Mode");
        ActionUnit basicMode = weapon.getActionUnitList().stream()
                .filter(au -> au.getName().equals("Basic Mode")).findFirst().orElse(null);

        Map<String, List<Targetable>> targetableMap = new HashMap<>();
        List<Targetable> targetableList = new ArrayList<>();
        // 1)
        System.out.println("1) Fail, target not visible");
        p5.getCharacterState().setTile(tileMap[1][2]);
        targetableList.add(p5);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);

        assertFalse(basicMode.check(game, targetableMap)); // target not visible

        targetableMap = new HashMap<>();
        targetableList = new ArrayList();

        // 2)
        System.out.println("2) System all green");
        p2.getCharacterState().setTile(tileMap[0][1]);
        targetableList.add(p2);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);

        assertTrue(basicMode.check(game, targetableMap));
        basicMode.run(game, targetableMap); // --> Basic Mode is cached in game now
        p2.getCharacterState().getDamageBar().stream()
                .forEach(pc -> assertEquals(p1.getColor(), pc));
        assertEquals(2,p2.getCharacterState().getDamageBar().size());
        assertEquals(1, p2.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());

        targetableMap = new HashMap<>();
        targetableList = new ArrayList<>();

        // testing Second Lock Effect --------------------------------------------------------------------
        System.out.println("Second Lock Effect");
        ActionUnit secondLock = weapon.getOptionalEffectList().stream()
                .filter(au -> au.getName().equals("Second Lock Effect")).findFirst().orElse(null);

        p2.getCharacterState().setTile(tileMap[0][1]);
        p3.getCharacterState().setTile(tileMap[1][2]);
        targetableList.add(p2);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);

        game.getCumulativeTargetSet().add(p2);
        System.out.println("1)p2 in CumulativeTargetList");
        assertFalse(secondLock.check(game, targetableMap)); // p2 in CumulativeTargetList

        targetableList = new ArrayList();
        targetableList.add(p3);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);
        System.out.println("p3 not in CumulativeTargetList");
        System.out.println("2)p3 is not visibile");
        assertFalse(secondLock.check(game, targetableMap)); // p3 not visible
        p3.getCharacterState().setTile(tileMap[1][0]);

        System.out.println("3)Not enough ammo");
        assertFalse(secondLock.check(game, targetableMap)); // not enough ammo
        p1.getCharacterState().getAmmoBag().put(AmmoColor.RED, 1);

        game.getCurrentActionUnitsList().remove(basicMode);
        System.out.println("4)Basic Mode not in currentActionUnitsList");
        assertFalse(secondLock.check(game, targetableMap)); // Basic Mode not in currentActionUnitsList
        game.getCurrentActionUnitsList().add(basicMode);

        System.out.println("5)System all green");
        assertTrue(secondLock.check(game, targetableMap));
        secondLock.run(game, targetableMap);

        assertEquals(2,p2.getCharacterState().getDamageBar().size());
        assertEquals(1, p2.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        assertEquals(1, p3.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        assertEquals(0, p1.getCharacterState().getAmmoBag().get(AmmoColor.RED).intValue());

        p2.getCharacterState().resetDamageBar();
        p2.getCharacterState().resetMarkerBar();
        p3.getCharacterState().resetDamageBar();
        p3.getCharacterState().resetMarkerBar();
        game.getCurrentActionUnitsList().clear();

    }

    @Test
    public void testMachineGun() {
        Weapon weapon = weaponList.stream().filter(w -> w.getName().equals("Machine_Gun")).findFirst().orElse(null);
        System.out.println(weapon.getName());

        game.setCurrentPlayer(p1); // the attacker
        p1.getCharacterState().setTile(tileMap[0][0]);
        p1.getCharacterState().addWeapon(weapon);
        p2.getCharacterState().resetDamageBar();
        p2.getCharacterState().resetMarkerBar();
        p3.getCharacterState().resetDamageBar();
        p3.getCharacterState().resetMarkerBar();
        p4.getCharacterState().resetDamageBar();
        p4.getCharacterState().resetMarkerBar();
        p5.getCharacterState().resetDamageBar();
        p5.getCharacterState().resetMarkerBar();

        // testing the availability of the action units
        assertTrue(weapon.isLoaded());
        assertTrue(weapon.getActionUnitList().stream()
                .map(ActionUnit::getName)
                .collect(Collectors.toList()).containsAll(Arrays.asList("Basic Mode"))
        );
        assertTrue(weapon.getOptionalEffectList().stream()
                .map(ActionUnit::getName)
                .collect(Collectors.toList()).containsAll(Arrays.asList("Focus Shot Effect", "Turret Tripod Effect"))
        );

        // testing Basic Mode --------------------------------------------------------------------
        System.out.println("Basic Mode ---------------------------------------------------");
        ActionUnit basicMode = weapon.getActionUnitList().stream()
                .filter(au -> au.getName().equals("Basic Mode")).findFirst().orElse(null);

        Map<String, List<Targetable>> targetableMap = new HashMap<>();
        List<Targetable> targetableList = new ArrayList<>();

        // 1)
        System.out.println("1) Fail, target not visible");
        p2.getCharacterState().setTile(tileMap[0][1]);
        p3.getCharacterState().setTile(tileMap[1][2]);
        targetableList.add(p2);
        targetableList.add(p3);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);

        assertFalse(basicMode.check(game, targetableMap)); // target not visible

        targetableMap = new HashMap<>();
        targetableList = new ArrayList();
        // 2)
        System.out.println("2) System all green");
        p2.getCharacterState().setTile(tileMap[0][1]);
        p3.getCharacterState().setTile(tileMap[1][1]);
        targetableList.add(p2);
        targetableList.add(p3);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);

        assertTrue(basicMode.check(game, targetableMap));
        basicMode.run(game, targetableMap);
        p2.getCharacterState().getDamageBar()
                .forEach(pc -> assertEquals(p1.getColor(), pc));
        assertEquals(1,p2.getCharacterState().getDamageBar().size());
        assertEquals(0, p2.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        p3.getCharacterState().getDamageBar()
                .forEach(pc -> assertEquals(p1.getColor(), pc));
        assertEquals(1,p3.getCharacterState().getDamageBar().size());
        assertEquals(0, p3.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());

        targetableMap = new HashMap<>();
        targetableList = new ArrayList<>();


        // testing Focus Shot Effect --------------------------------------------------------------------
        System.out.println("Testing Focus Shot Effect ---------------------------------------------------");
        ActionUnit focusShot = weapon.getOptionalEffectList().stream()
                .filter(au -> au.getName().equals("Focus Shot Effect")).findFirst().orElse(null);

        p2.getCharacterState().setTile(tileMap[0][1]);
        p3.getCharacterState().setTile(tileMap[1][1]);
        targetableList.add(p2);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);

        System.out.println("1) Not enough ammo");
        assertFalse(focusShot.check(game,targetableMap));
        p1.getCharacterState().getAmmoBag().put(AmmoColor.YELLOW, 1);

        game.getCurrentActionUnitsList().remove(basicMode);
        System.out.println("2) Missing executed action units");
        assertFalse(focusShot.check(game,targetableMap));
        game.getCurrentActionUnitsList().add(basicMode);

        System.out.println("3) System all green");
        assertTrue(focusShot.check(game,targetableMap));
        focusShot.run(game, targetableMap);
        p2.getCharacterState().getDamageBar()
                .forEach(pc -> assertEquals(p1.getColor(), pc));
        assertEquals(2,p2.getCharacterState().getDamageBar().size());
        assertEquals(0, p2.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        p3.getCharacterState().getDamageBar()
                .forEach(pc -> assertEquals(p1.getColor(), pc));
        assertEquals(1,p3.getCharacterState().getDamageBar().size());
        assertEquals(0, p3.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        assertEquals(0, p1.getCharacterState().getAmmoBag().get(AmmoColor.YELLOW).intValue());

        targetableMap = new HashMap<>();
        targetableList = new ArrayList<>();

        // testing Turret Tripod Effect --------------------------------------------------------------------
        System.out.println("Testing Turret Tripod Effect ---------------------------------------------------");
        ActionUnit turretTripod = weapon.getOptionalEffectList().stream()
                .filter(au -> au.getName().equals("Turret Tripod Effect")).findFirst().orElse(null);

        p2.getCharacterState().setTile(tileMap[0][1]);
        p3.getCharacterState().setTile(tileMap[1][1]);
        targetableList.add(p3);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);

        System.out.println("1) Not enough ammo");
        assertFalse(turretTripod.check(game,targetableMap));
        p1.getCharacterState().getAmmoBag().put(AmmoColor.BLUE, 1);

        game.getCurrentActionUnitsList().remove(basicMode);
        System.out.println("2) Missing executed action units");
        assertFalse(turretTripod.check(game,targetableMap));
        game.getCurrentActionUnitsList().add(basicMode);

        System.out.println("3) System all green");
        assertTrue(turretTripod.check(game,targetableMap));
        turretTripod.run(game, targetableMap);
        p2.getCharacterState().getDamageBar()
                .forEach(pc -> assertEquals(p1.getColor(), pc));
        assertEquals(2,p2.getCharacterState().getDamageBar().size());
        assertEquals(0, p2.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        p3.getCharacterState().getDamageBar()
                .forEach(pc -> assertEquals(p1.getColor(), pc));
        assertEquals(2,p3.getCharacterState().getDamageBar().size());
        assertEquals(0, p3.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        assertEquals(0, p1.getCharacterState().getAmmoBag().get(AmmoColor.BLUE).intValue());

        // testing Turret Tripod Effect 2nd option ------------------------------------------------
        targetableMap = new HashMap<>();
        targetableList = new ArrayList<>();

        game.getCurrentActionUnitsList().remove(focusShot); // <-- should work even without this second effect
        p4.getCharacterState().setTile(tileMap[1][1]);
        targetableList.add(p4);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);
        p1.getCharacterState().getAmmoBag().put(AmmoColor.BLUE, 1);
        System.out.println("4) System all green [other option]");
        assertTrue(turretTripod.check(game,targetableMap));
        turretTripod.run(game, targetableMap);
        p2.getCharacterState().getDamageBar()
                .forEach(pc -> assertEquals(p1.getColor(), pc));
        assertEquals(2,p2.getCharacterState().getDamageBar().size());
        assertEquals(0, p2.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        p3.getCharacterState().getDamageBar()
                .forEach(pc -> assertEquals(p1.getColor(), pc));
        assertEquals(2,p3.getCharacterState().getDamageBar().size());
        assertEquals(0, p3.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        assertEquals(1,p4.getCharacterState().getDamageBar().size());
        assertEquals(0, p4.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        assertEquals(0, p1.getCharacterState().getAmmoBag().get(AmmoColor.BLUE).intValue());

        p2.getCharacterState().resetDamageBar();
        p2.getCharacterState().resetMarkerBar();
        p3.getCharacterState().resetDamageBar();
        p3.getCharacterState().resetMarkerBar();
        p4.getCharacterState().resetDamageBar();
        p4.getCharacterState().resetMarkerBar();
        game.getCurrentActionUnitsList().clear();
    }

    @Test
    public void testTHOR() {
        Weapon weapon = weaponList.stream().filter(w -> w.getName().equals("THOR")).findFirst().orElse(null);
        System.out.println(weapon.getName());

        game.setCurrentPlayer(p1); // the attacker
        p1.getCharacterState().setTile(tileMap[0][0]);
		p1.getCharacterState().addWeapon(weapon);
        p2.getCharacterState().resetDamageBar();
        p2.getCharacterState().resetMarkerBar();
        p3.getCharacterState().resetDamageBar();
        p3.getCharacterState().resetMarkerBar();
        p4.getCharacterState().resetDamageBar();
        p4.getCharacterState().resetMarkerBar();
        p5.getCharacterState().resetDamageBar();
        p5.getCharacterState().resetMarkerBar();

        // testing the availability of the action units
        assertTrue(weapon.isLoaded());
        assertTrue(weapon.getActionUnitList().stream()
                .map(ActionUnit::getName)
                .collect(Collectors.toList()).containsAll(Arrays.asList("Basic Mode"))
        );
        assertTrue(weapon.getOptionalEffectList().stream()
                .map(ActionUnit::getName)
                .collect(Collectors.toList()).containsAll(Arrays.asList("Chain Reaction Effect", "High Voltage Effect"))
        );

        // testing Basic Mode --------------------------------------------------------------------
        System.out.println("Basic Mode ---------------------------------------------------");
        ActionUnit basicMode = weapon.getActionUnitList().stream()
                .filter(au -> au.getName().equals("Basic Mode")).findFirst().orElse(null);

        Map<String, List<Targetable>> targetableMap = new HashMap<>();
        List<Targetable> targetableList = new ArrayList<>();

        // 1)
        System.out.println("1) Fail, target not visible");
        p2.getCharacterState().setTile(tileMap[1][2]);
        targetableList.add(p2);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);

        assertFalse(basicMode.check(game, targetableMap)); // target not visible

        targetableMap = new HashMap<>();
        targetableList = new ArrayList();
        // 2)
        System.out.println("2) System all green");
        p2.getCharacterState().setTile(tileMap[0][1]);
        targetableList.add(p2);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);

        assertTrue(basicMode.check(game, targetableMap));
        basicMode.run(game, targetableMap);
        p2.getCharacterState().getDamageBar()
                .forEach(pc -> assertEquals(p1.getColor(), pc));
        assertEquals(2,p2.getCharacterState().getDamageBar().size());
        assertEquals(0, p2.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());

        targetableMap = new HashMap<>();
        targetableList = new ArrayList<>();

        // testing Chain Reaction Effect --------------------------------------------------------------------
        System.out.println("Testing Chain Reaction Effect ------------------------------------------------");
        ActionUnit chainReaction = weapon.getOptionalEffectList().stream()
                .filter(au -> au.getName().equals("Chain Reaction Effect")).findFirst().orElse(null);

        p3.getCharacterState().setTile(tileMap[1][2]);
        targetableList.add(p3);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);

        System.out.println("1) Not enough ammo");
        assertFalse(chainReaction.check(game,targetableMap));
        p1.getCharacterState().getAmmoBag().put(AmmoColor.BLUE, 1);

        game.getCurrentActionUnitsList().remove(basicMode);
        System.out.println("2) Missing executed action units");
        assertFalse(chainReaction.check(game,targetableMap));
        game.getCurrentActionUnitsList().add(basicMode);

        System.out.println("3) Target not visible");
        assertFalse(chainReaction.check(game,targetableMap));
        p3.getCharacterState().setTile(tileMap[1][1]);

        System.out.println("4) System all green");
        assertTrue(chainReaction.check(game,targetableMap));
        chainReaction.run(game, targetableMap);
        p2.getCharacterState().getDamageBar()
                .forEach(pc -> assertEquals(p1.getColor(), pc));
        assertEquals(2,p2.getCharacterState().getDamageBar().size());
        assertEquals(0, p2.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        p3.getCharacterState().getDamageBar()
                .forEach(pc -> assertEquals(p1.getColor(), pc));
        assertEquals(1,p3.getCharacterState().getDamageBar().size());
        assertEquals(0, p3.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        assertEquals(0, p1.getCharacterState().getAmmoBag().get(AmmoColor.BLUE).intValue());

        targetableMap = new HashMap<>();
        targetableList = new ArrayList<>();

        // testing High Voltage Effect --------------------------------------------------------------------
        System.out.println("Testing High Voltage Effect ---------------------------------------------------");
        ActionUnit highVoltage = weapon.getOptionalEffectList().stream()
                .filter(au -> au.getName().equals("High Voltage Effect")).findFirst().orElse(null);

        p4.getCharacterState().setTile(tileMap[1][2]);
        targetableList.add(p4);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);

        System.out.println("1) Not enough ammo");
        assertFalse(highVoltage.check(game,targetableMap));
        p1.getCharacterState().getAmmoBag().put(AmmoColor.BLUE, 1);

        game.getCurrentActionUnitsList().remove(basicMode);
        System.out.println("2) Missing executed action units");
        assertFalse(highVoltage.check(game,targetableMap));
        game.getCurrentActionUnitsList().add(basicMode);

        System.out.println("3) Target not visible");
        assertFalse(highVoltage.check(game,targetableMap));
        p4.getCharacterState().setTile(tileMap[1][1]);

        System.out.println("4) System all green");
        assertTrue(highVoltage.check(game,targetableMap));
        highVoltage.run(game, targetableMap);
        p2.getCharacterState().getDamageBar()
                .forEach(pc -> assertEquals(p1.getColor(), pc));
        assertEquals(2,p2.getCharacterState().getDamageBar().size());
        assertEquals(0, p2.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        p3.getCharacterState().getDamageBar()
                .forEach(pc -> assertEquals(p1.getColor(), pc));
        assertEquals(1,p3.getCharacterState().getDamageBar().size());
        assertEquals(0, p3.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        assertEquals(0, p1.getCharacterState().getAmmoBag().get(AmmoColor.BLUE).intValue());
        p4.getCharacterState().getDamageBar()
                .forEach(pc -> assertEquals(p1.getColor(), pc));
        assertEquals(2, p4.getCharacterState().getDamageBar().size());
        assertEquals(0, p4.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        assertEquals(0, p1.getCharacterState().getAmmoBag().get(AmmoColor.BLUE).intValue());


        // testing High Voltage Effect other scenarios ------------------------------------------------
        targetableMap = new HashMap<>();
        targetableList = new ArrayList<>();

        game.getCurrentActionUnitsList().remove(chainReaction); // <-- should not work without this second effect
        p4.getCharacterState().setTile(tileMap[1][1]);
        targetableList.add(p4);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);
        p1.getCharacterState().getAmmoBag().put(AmmoColor.BLUE, 1);
        System.out.println("5) Missing an executed action unit");
        assertFalse(highVoltage.check(game,targetableMap));
        game.getCurrentActionUnitsList().add(chainReaction);

        targetableMap = new HashMap<>();
        targetableList = new ArrayList<>();
        p3.getCharacterState().setTile(tileMap[1][1]);
        targetableList.add(p3);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);
        System.out.println("6) Tartget in Target List");
        assertFalse(highVoltage.check(game,targetableMap));

        targetableMap = new HashMap<>();
        targetableList = new ArrayList<>();
        p2.getCharacterState().setTile(tileMap[1][1]);
        targetableList.add(p2);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);
        System.out.println("6bis) Tartget in Target List");
        assertFalse(highVoltage.check(game,targetableMap));

        p2.getCharacterState().resetDamageBar();
        p2.getCharacterState().resetMarkerBar();
        p3.getCharacterState().resetDamageBar();
        p3.getCharacterState().resetMarkerBar();
        p4.getCharacterState().resetDamageBar();
        p4.getCharacterState().resetMarkerBar();
        game.getCurrentActionUnitsList().clear();
    }

    @Test
    public void testPlasmaGun() {
        Weapon weapon = weaponList.stream().filter(w -> w.getName().equals("Plasma_Gun")).findFirst().orElse(null);
        System.out.println(weapon.getName());

        game.setCurrentPlayer(p1); // the attacker
        p1.getCharacterState().setTile(tileMap[0][0]);
		p1.getCharacterState().addWeapon(weapon);
        p2.getCharacterState().resetDamageBar();
        p2.getCharacterState().resetMarkerBar();
        p3.getCharacterState().resetDamageBar();
        p3.getCharacterState().resetMarkerBar();
        p4.getCharacterState().resetDamageBar();
        p4.getCharacterState().resetMarkerBar();
        p5.getCharacterState().resetDamageBar();
        p5.getCharacterState().resetMarkerBar();


        // testing the availability of the action units
        assertTrue(weapon.isLoaded());
        assertTrue(weapon.getActionUnitList().stream()
                .map(ActionUnit::getName)
                .collect(Collectors.toList()).containsAll(Arrays.asList("Basic Mode"))
        );
        assertTrue(weapon.getOptionalEffectList().stream()
                .map(ActionUnit::getName)
                .collect(Collectors.toList()).containsAll(Arrays.asList("Phase Glide Effect", "Charged Shot Effect"))
        );

        // testing Basic Mode --------------------------------------------------------------------
        System.out.println("Basic Mode --------------------------------------------------------------------");
        ActionUnit basicMode = weapon.getActionUnitList().stream()
                .filter(au -> au.getName().equals("Basic Mode")).findFirst().orElse(null);

        Map<String, List<Targetable>> targetableMap = new HashMap<>();
        List<Targetable> targetableList = new ArrayList<>();

        // 1)
        System.out.println("1) Fail, target not visible");
        p2.getCharacterState().setTile(tileMap[1][2]);
        targetableList.add(p2);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);

        assertFalse(basicMode.check(game, targetableMap)); // target not visible

        targetableMap = new HashMap<>();
        targetableList = new ArrayList();
        // 2)
        System.out.println("2) System all green");
        p2.getCharacterState().setTile(tileMap[0][1]);
        targetableList.add(p2);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);

        assertTrue(basicMode.check(game, targetableMap));
        basicMode.run(game, targetableMap);
        p2.getCharacterState().getDamageBar()
                .forEach(pc -> assertEquals(p1.getColor(), pc));
        assertEquals(2,p2.getCharacterState().getDamageBar().size());
        assertEquals(0, p2.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());

        targetableMap = new HashMap<>();
        targetableList = new ArrayList<>();

        // testing Phase Glide Effect --------------------------------------------------------------------
        System.out.println("Testing Phase Glide Effect ------------------------------------------------------------");
        ActionUnit phaseGlide = weapon.getOptionalEffectList().stream()
                .filter(au -> au.getName().equals("Phase Glide Effect")).findFirst().orElse(null);

        targetableList.add(tileMap[1][2]);
        targetableMap.put(CommandConstants.TILELIST, targetableList);

        System.out.println("1) Too far");
        assertFalse(phaseGlide.check(game, targetableMap));
        targetableMap = new HashMap<>();
        targetableList = new ArrayList<>();
        targetableList.add(tileMap[1][1]);
        targetableMap.put(CommandConstants.TILELIST, targetableList);

        System.out.println("2) Basic Mode was performed");
        assertTrue(phaseGlide.check(game, targetableMap));

        p2.getCharacterState().setTile(tileMap[1][2]);
        p3.getCharacterState().setTile(tileMap[1][2]);
        p4.getCharacterState().setTile(tileMap[1][2]);
        p5.getCharacterState().setTile(tileMap[1][2]);
        game.getCurrentActionUnitsList().remove(basicMode); // <-- no basic mode, needs to check CanShoot
        System.out.println("3) No Basic Mode performed, no targets");
        assertFalse(phaseGlide.check(game, targetableMap));
        p2.getCharacterState().setTile(tileMap[1][0]);

        System.out.println("4) System all green");
        assertTrue(phaseGlide.check(game,targetableMap));
        phaseGlide.run(game, targetableMap);
        p2.getCharacterState().getDamageBar()
                .forEach(pc -> assertEquals(p1.getColor(), pc));
        assertEquals(2,p2.getCharacterState().getDamageBar().size());
        assertEquals(0, p2.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        assertTrue(p1.getCharacterState().getTile().equals(tileMap[1][1]));

        targetableMap = new HashMap<>();
        targetableList = new ArrayList<>();

        // testing Charged Shot Effect --------------------------------------------------------------------
        System.out.println("Testing Charged Shot Effect ---------------------------------------------------");
        ActionUnit chargedShot = weapon.getOptionalEffectList().stream()
                .filter(au -> au.getName().equals("Charged Shot Effect")).findFirst().orElse(null);

        System.out.println("1) Not enough ammo");
        assertFalse(chargedShot.check(game,targetableMap));
        p1.getCharacterState().getAmmoBag().put(AmmoColor.BLUE, 1);

        game.getCurrentActionUnitsList().remove(basicMode);
        System.out.println("2) Missing executed action units");
        assertFalse(chargedShot.check(game,targetableMap));
        game.getCurrentActionUnitsList().add(basicMode);

        System.out.println("3) System all green");
        assertTrue(chargedShot.check(game,targetableMap));
        chargedShot.run(game, targetableMap);
        p2.getCharacterState().getDamageBar()
                .forEach(pc -> assertEquals(p1.getColor(), pc));
        assertEquals(3, p2.getCharacterState().getDamageBar().size());
        assertEquals(0, p2.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());

        p2.getCharacterState().resetDamageBar();
        p2.getCharacterState().resetMarkerBar();
        p3.getCharacterState().resetDamageBar();
        p3.getCharacterState().resetMarkerBar();
        p4.getCharacterState().resetDamageBar();
        p4.getCharacterState().resetMarkerBar();
        game.getCurrentActionUnitsList().clear();
    }

    @Test
    public void testWhisper() {
        Weapon weapon = weaponList.stream().filter(w -> w.getName().equals("Whisper")).findFirst().orElse(null);
        System.out.println(weapon.getName());

        game.setCurrentPlayer(p1); // the attacker
        p1.getCharacterState().setTile(tileMap[0][0]);
		p1.getCharacterState().addWeapon(weapon);
        p2.getCharacterState().resetDamageBar();
        p2.getCharacterState().resetMarkerBar();
        p3.getCharacterState().resetDamageBar();
        p3.getCharacterState().resetMarkerBar();
        p4.getCharacterState().resetDamageBar();
        p4.getCharacterState().resetMarkerBar();
        p5.getCharacterState().resetDamageBar();
        p5.getCharacterState().resetMarkerBar();

        // testing the availability of the action units
        assertTrue(weapon.isLoaded());
        assertTrue(weapon.getActionUnitList().stream()
                .map(ActionUnit::getName)
                .collect(Collectors.toList()).containsAll(Arrays.asList("Basic Mode"))
        );
        assertTrue(weapon.getOptionalEffectList().stream()
                .map(ActionUnit::getName)
                .collect(Collectors.toList()).containsAll(Arrays.asList())
        );

        // testing Basic Mode --------------------------------------------------------------------
        System.out.println("Basic Mode --------------------------------------------------------------------");
        ActionUnit basicMode = weapon.getActionUnitList().stream()
                .filter(au -> au.getName().equals("Basic Mode")).findFirst().orElse(null);

        Map<String, List<Targetable>> targetableMap = new HashMap<>();
        List<Targetable> targetableList = new ArrayList<>();

        // 1)
        System.out.println("1) Fail, target not visible");
        p2.getCharacterState().setTile(tileMap[1][2]);
        targetableList.add(p2);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);

        assertFalse(basicMode.check(game, targetableMap)); // target not visible

        targetableMap = new HashMap<>();
        targetableList = new ArrayList();
        // 2)
        System.out.println("2) Min distance violated");
        p2.getCharacterState().setTile(tileMap[0][1]);
        targetableList.add(p2);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);
        assertFalse(basicMode.check(game, targetableMap));
        targetableMap = new HashMap<>();
        targetableList = new ArrayList();

        // 3)
        System.out.println("3) System all green");
        p2.getCharacterState().setTile(tileMap[0][2]);
        targetableList.add(p2);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);
        assertTrue(basicMode.check(game, targetableMap));
        basicMode.run(game, targetableMap);
        p2.getCharacterState().getDamageBar()
                .forEach(pc -> assertEquals(p1.getColor(), pc));
        assertEquals(3,p2.getCharacterState().getDamageBar().size());
        assertEquals(1, p2.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());

        p2.getCharacterState().resetDamageBar();
        p2.getCharacterState().resetMarkerBar();
        game.getCurrentActionUnitsList().clear();
    }

    @Test
    public void testElectroscythe() {
        Weapon weapon = weaponList.stream().filter(w -> w.getName().equals("Electroscythe")).findFirst().orElse(null);
        System.out.println(weapon.getName());

        game.setCurrentPlayer(p1); // the attacker
        p1.getCharacterState().setTile(tileMap[0][0]);
		p1.getCharacterState().addWeapon(weapon);
        p2.getCharacterState().resetDamageBar();
        p2.getCharacterState().resetMarkerBar();
        p3.getCharacterState().resetDamageBar();
        p3.getCharacterState().resetMarkerBar();
        p4.getCharacterState().resetDamageBar();
        p4.getCharacterState().resetMarkerBar();
        p5.getCharacterState().resetDamageBar();
        p5.getCharacterState().resetMarkerBar();

        // testing the availability of the action units
        assertTrue(weapon.isLoaded());
        assertTrue(weapon.getActionUnitList().stream()
                .map(ActionUnit::getName)
                .collect(Collectors.toList()).containsAll(Arrays.asList("Basic Mode", "Reaper Mode"))
        );
        assertTrue(weapon.getOptionalEffectList().stream()
                .map(ActionUnit::getName)
                .collect(Collectors.toList()).containsAll(Arrays.asList())
        );

        // testing Basic Mode --------------------------------------------------------------------
        System.out.println("Basic Mode --------------------------------------------------------------------");
        ActionUnit basicMode = weapon.getActionUnitList().stream()
                .filter(au -> au.getName().equals("Basic Mode")).findFirst().orElse(null);

        Map<String, List<Targetable>> targetableMap = new HashMap<>();
        List<Targetable> targetableList = new ArrayList<>();

        // 1)
        System.out.println("1) Fail, no targets in attacker's tile");
        p2.getCharacterState().setTile(tileMap[1][2]);
        p3.getCharacterState().setTile(tileMap[1][2]);
        p4.getCharacterState().setTile(tileMap[1][2]);
        p5.getCharacterState().setTile(tileMap[1][2]);

        assertFalse(basicMode.check(game, targetableMap));
        // 2)
        System.out.println("2) System all green");
        p2.getCharacterState().setTile(tileMap[0][0]);
        p3.getCharacterState().setTile(tileMap[0][0]);
        p4.getCharacterState().setTile(tileMap[0][0]);
        p5.getCharacterState().setTile(tileMap[1][0]);
        assertTrue(basicMode.check(game, targetableMap));
        basicMode.run(game, targetableMap);
        p2.getCharacterState().getDamageBar()
                .forEach(pc -> assertEquals(p1.getColor(), pc));
        assertEquals(1,p2.getCharacterState().getDamageBar().size());
        assertEquals(0, p2.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        assertEquals(1,p3.getCharacterState().getDamageBar().size());
        assertEquals(0, p3.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        assertEquals(1,p4.getCharacterState().getDamageBar().size());
        assertEquals(0, p4.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        assertEquals(0,p5.getCharacterState().getDamageBar().size());
        assertEquals(0, p5.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());

        // testing Reaper Mode --------------------------------------------------------------------
        System.out.println("Reaper Mode --------------------------------------------------------------------");
        ActionUnit reaperMode = weapon.getActionUnitList().stream()
                .filter(au -> au.getName().equals("Reaper Mode")).findFirst().orElse(null);

        targetableMap = new HashMap<>();
        targetableList = new ArrayList<>();
        p2.getCharacterState().resetDamageBar();
        p2.getCharacterState().resetMarkerBar();
        p3.getCharacterState().resetDamageBar();
        p3.getCharacterState().resetMarkerBar();
        p4.getCharacterState().resetDamageBar();
        p4.getCharacterState().resetMarkerBar();
        p5.getCharacterState().resetDamageBar();
        p5.getCharacterState().resetMarkerBar();

        // 1)
        System.out.println("1) Fail, no targets in attacker's tile");
        p2.getCharacterState().setTile(tileMap[1][2]);
        p3.getCharacterState().setTile(tileMap[1][2]);
        p4.getCharacterState().setTile(tileMap[1][2]);
        p5.getCharacterState().setTile(tileMap[1][2]);
        assertFalse(reaperMode.check(game, targetableMap));

        // 2)
        System.out.println("2) Not enough Ammo");
        p2.getCharacterState().setTile(tileMap[0][0]);
        p3.getCharacterState().setTile(tileMap[0][0]);
        p4.getCharacterState().setTile(tileMap[0][0]);
        p5.getCharacterState().setTile(tileMap[1][0]);
        assertFalse(reaperMode.check(game, targetableMap));

        // 3)
        System.out.println("3) System all green");
        p1.getCharacterState().getAmmoBag().put(AmmoColor.RED, 1);
        p1.getCharacterState().getAmmoBag().put(AmmoColor.BLUE, 1);
        assertTrue(reaperMode.check(game, targetableMap));
        reaperMode.run(game, targetableMap);
        p2.getCharacterState().getDamageBar()
                .forEach(pc -> assertEquals(p1.getColor(), pc));
        assertEquals(2,p2.getCharacterState().getDamageBar().size());
        assertEquals(0, p2.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        assertEquals(2,p3.getCharacterState().getDamageBar().size());
        assertEquals(0, p3.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        assertEquals(2,p4.getCharacterState().getDamageBar().size());
        assertEquals(0, p4.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        assertEquals(0,p5.getCharacterState().getDamageBar().size());
        assertEquals(0, p5.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        assertEquals(0, p1.getCharacterState().getAmmoBag().get(AmmoColor.BLUE).intValue());
        assertEquals(0, p1.getCharacterState().getAmmoBag().get(AmmoColor.RED).intValue());

        p2.getCharacterState().resetDamageBar();
        p2.getCharacterState().resetMarkerBar();
        p3.getCharacterState().resetDamageBar();
        p3.getCharacterState().resetMarkerBar();
        p4.getCharacterState().resetDamageBar();
        p4.getCharacterState().resetMarkerBar();
        p5.getCharacterState().resetDamageBar();
        p5.getCharacterState().resetMarkerBar();
        game.getCurrentActionUnitsList().clear();
    }

    @Test
    public void testTractorBeam() {
        Weapon weapon = weaponList.stream().filter(w -> w.getName().equals("Tractor_Beam")).findFirst().orElse(null);
        System.out.println(weapon.getName());

        game.setCurrentPlayer(p1); // the attacker
        p1.getCharacterState().setTile(tileMap[0][0]);
		p1.getCharacterState().addWeapon(weapon);
        p2.getCharacterState().resetDamageBar();
        p2.getCharacterState().resetMarkerBar();
        p3.getCharacterState().resetDamageBar();
        p3.getCharacterState().resetMarkerBar();
        p4.getCharacterState().resetDamageBar();
        p4.getCharacterState().resetMarkerBar();
        p5.getCharacterState().resetDamageBar();
        p5.getCharacterState().resetMarkerBar();

        // testing the availability of the action units
        assertTrue(weapon.isLoaded());
        assertTrue(weapon.getActionUnitList().stream()
                .map(ActionUnit::getName)
                .collect(Collectors.toList()).containsAll(Arrays.asList("Basic Mode", "Punisher Mode"))
        );
        assertTrue(weapon.getOptionalEffectList().stream()
                .map(ActionUnit::getName)
                .collect(Collectors.toList()).containsAll(Arrays.asList())
        );

        // testing Basic Mode --------------------------------------------------------------------
        System.out.println("Basic Mode --------------------------------------------------------------------");
        ActionUnit basicMode = weapon.getActionUnitList().stream()
                .filter(au -> au.getName().equals("Basic Mode")).findFirst().orElse(null);

        Map<String, List<Targetable>> targetableMap = new HashMap<>();
        List<Targetable> targetableList = new ArrayList<>();

        // 1)
        System.out.println("1) Fail, final tile is not visible");
        targetableList.add(tileMap[1][2]);
        targetableMap.put(CommandConstants.TILELIST, targetableList);
        assertFalse(basicMode.check(game,targetableMap));

        targetableMap = new HashMap<>();
        targetableList = new ArrayList();

        // 2)
        System.out.println("2) Fail, max distance violated");
        targetableList.add(tileMap[0][3]); // <-- solves final tile visibility violation
        targetableMap.put(CommandConstants.TILELIST, targetableList);
        targetableList = new ArrayList<>();
        p2.getCharacterState().setTile(tileMap[1][1]);
        targetableList.add(p2);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);
        assertFalse(basicMode.check(game,targetableMap));

        targetableList = new ArrayList();

        // 3)
        System.out.println("3) System all green");
        targetableList.add(tileMap[0][2]); // <-- solves final tile distance violation
        targetableMap.put(CommandConstants.TILELIST, targetableList);
        assertTrue(basicMode.check(game, targetableMap));
        basicMode.run(game, targetableMap);
        p2.getCharacterState().getDamageBar()
                .forEach(pc -> assertEquals(p1.getColor(), pc));
        assertEquals(1,p2.getCharacterState().getDamageBar().size());
        assertEquals(0, p2.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        assertEquals(tileMap[0][2], p2.getCharacterState().getTile());


        targetableMap = new HashMap<>();
        p2.getCharacterState().resetDamageBar();
        p2.getCharacterState().resetMarkerBar();

        // testing Punisher Mode --------------------------------------------------------------------
        System.out.println("Punisher Mode --------------------------------------------------------------------");
        ActionUnit punisherMode = weapon.getActionUnitList().stream()
                .filter(au -> au.getName().equals("Punisher Mode")).findFirst().orElse(null);

        p2.getCharacterState().resetDamageBar();
        p2.getCharacterState().resetMarkerBar();
        // 1)
        System.out.println("1) Fail, not enough ammo");
        assertFalse(punisherMode.check(game,targetableMap));
        p1.getCharacterState().getAmmoBag().put(AmmoColor.RED,1);
        p1.getCharacterState().getAmmoBag().put(AmmoColor.YELLOW,1);

        targetableMap = new HashMap<>();
        targetableList = new ArrayList();

        // 2)
        System.out.println("2) Fail, max distance violated");
        p2.getCharacterState().setTile(tileMap[1][2]);
        targetableList.add(p2);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);
        assertFalse(punisherMode.check(game,targetableMap));

        targetableList = new ArrayList();

        // 3)
        System.out.println("3) System all green");
        p2.getCharacterState().setTile(tileMap[0][1]); // <-- no distance violation
        targetableList.add(p2);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);
        assertTrue(punisherMode.check(game, targetableMap));
        punisherMode.run(game, targetableMap);
        p2.getCharacterState().getDamageBar()
                .forEach(pc -> assertEquals(p1.getColor(), pc));
        assertEquals(3,p2.getCharacterState().getDamageBar().size());
        assertEquals(0, p2.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        assertEquals(tileMap[0][0], p1.getCharacterState().getTile());
        assertEquals(0, p1.getCharacterState().getAmmoBag().get(AmmoColor.RED).intValue());
        assertEquals(0, p1.getCharacterState().getAmmoBag().get(AmmoColor.YELLOW).intValue());

        p2.getCharacterState().resetDamageBar();
        p2.getCharacterState().resetMarkerBar();
        p3.getCharacterState().resetDamageBar();
        p3.getCharacterState().resetMarkerBar();
        p4.getCharacterState().resetDamageBar();
        p4.getCharacterState().resetMarkerBar();
        p5.getCharacterState().resetDamageBar();
        p5.getCharacterState().resetMarkerBar();
        game.getCurrentActionUnitsList().clear();

    }

    @Test
    public void testVortexCannon() {
        Weapon weapon = weaponList.stream().filter(w -> w.getName().equals("Vortex_Cannon")).findFirst().orElse(null);
        System.out.println(weapon.getName());

        game.setCurrentPlayer(p1); // the attacker
        p1.getCharacterState().setTile(tileMap[0][0]);
		p1.getCharacterState().addWeapon(weapon);
        p2.getCharacterState().resetDamageBar();
        p2.getCharacterState().resetMarkerBar();
        p3.getCharacterState().resetDamageBar();
        p3.getCharacterState().resetMarkerBar();
        p4.getCharacterState().resetDamageBar();
        p4.getCharacterState().resetMarkerBar();
        p5.getCharacterState().resetDamageBar();
        p5.getCharacterState().resetMarkerBar();

        // testing the availability of the action units
        assertTrue(weapon.isLoaded());
        assertTrue(weapon.getActionUnitList().stream()
                .map(ActionUnit::getName)
                .collect(Collectors.toList()).containsAll(Arrays.asList("Basic Mode"))
        );
        assertTrue(weapon.getOptionalEffectList().stream()
                .map(ActionUnit::getName)
                .collect(Collectors.toList()).containsAll(Arrays.asList("Black Hole Effect"))
        );

        // testing Basic Mode --------------------------------------------------------------------
        System.out.println("Basic Mode --------------------------------------------------------------------");
        ActionUnit basicMode = weapon.getActionUnitList().stream()
                .filter(au -> au.getName().equals("Basic Mode")).findFirst().orElse(null);

        Map<String, List<Targetable>> targetableMap = new HashMap<>();
        List<Targetable> targetableList = new ArrayList<>();

        // 1)
        System.out.println("1) Fail, tile not visible");
        targetableList.add(tileMap[1][2]);
        targetableMap.put(CommandConstants.TILELIST, targetableList);
        assertFalse(basicMode.check(game, targetableMap));

        targetableList = new ArrayList<>();
        targetableMap = new HashMap<>();
        // 2)
        System.out.println("2) Fail, attacker tile");
        targetableList.add(tileMap[0][0]);
        targetableMap.put(CommandConstants.TILELIST, targetableList);
        assertFalse(basicMode.check(game, targetableMap));

        targetableList = new ArrayList<>();
        targetableMap = new HashMap<>();
        // 3)
        System.out.println("3) System all green");
        targetableList.add(tileMap[0][1]);
        targetableMap.put(CommandConstants.TILELIST, targetableList);
        targetableList = new ArrayList<>();
        p2.getCharacterState().setTile(tileMap[0][2]);
        targetableList.add(p2);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);
        assertTrue(basicMode.check(game, targetableMap));
        basicMode.run(game, targetableMap);
        p2.getCharacterState().getDamageBar()
                .forEach(pc -> assertEquals(p1.getColor(), pc));
        assertEquals(2,p2.getCharacterState().getDamageBar().size());
        assertEquals(0, p2.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        assertEquals(tileMap[0][1], p2.getCharacterState().getTile());
        p2.getCharacterState().resetDamageBar();
        p2.getCharacterState().resetMarkerBar();
        p2.getCharacterState().setTile(null);

        // testing Black Hole Effect --------------------------------------------------------------------
        System.out.println("Black Hole Effect --------------------------------------------------------------------");
        ActionUnit blackHole = weapon.getOptionalEffectList().stream()
                .filter(au -> au.getName().equals("Black Hole Effect")).findFirst().orElse(null);

        // 1)
        targetableList = new ArrayList<>();
        targetableMap = new HashMap<>();
        System.out.println("1) Fail, not enough ammo");
        System.out.println(blackHole);
        assertFalse(blackHole.check(game,targetableMap));
        p1.getCharacterState().getAmmoBag().put(AmmoColor.RED, 1);

        // 2)
        System.out.println("2) Fail, Basic Mode not exectued");
        game.getCurrentActionUnitsList().remove(basicMode);
        assertFalse(blackHole.check(game, targetableMap));
        game.getCurrentActionUnitsList().add(basicMode);

        // 3)
        System.out.println("3) Fail, max distance violated. Vortex at TileMap[0][1]");
        p2.getCharacterState().setTile(tileMap[0][1]);
        p3.getCharacterState().setTile(tileMap[1][0]);
        targetableList.add(p2);
        targetableList.add(p3);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);
        assertFalse(blackHole.check(game, targetableMap));

        // 4)
        System.out.println("4) Fail, target is in target list");
        targetableList = new ArrayList<>();
        targetableMap = new HashMap<>();
        p2.getCharacterState().setTile(tileMap[0][1]);
        p3.getCharacterState().setTile(tileMap[1][1]);
        targetableList.add(p2);
        targetableList.add(p3);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);
        assertFalse(blackHole.check(game, targetableMap));

        // 5)
        System.out.println("5) Fail, the targets in target list are the same");
        targetableList = new ArrayList<>();
        targetableMap = new HashMap<>();
        p2.getCharacterState().setTile(tileMap[0][1]);
        p3.getCharacterState().setTile(tileMap[1][1]);
        targetableList.add(p3);
        targetableList.add(p3);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);
        assertFalse(blackHole.check(game, targetableMap));

        // 6)
        System.out.println("6) System all green");
        targetableList = new ArrayList<>();
        targetableMap = new HashMap<>();
        p3.getCharacterState().setTile(tileMap[1][1]);
        p4.getCharacterState().setTile(tileMap[1][1]);
        targetableList.add(p3);
        targetableList.add(p4);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);
        assertTrue(blackHole.check(game, targetableMap));
        blackHole.run(game, targetableMap);
        p3.getCharacterState().getDamageBar()
                .forEach(pc -> assertEquals(p1.getColor(), pc));
        assertEquals(1,p3.getCharacterState().getDamageBar().size());
        assertEquals(0, p3.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        assertEquals(tileMap[0][1], p3.getCharacterState().getTile());
        p4.getCharacterState().getDamageBar()
                .forEach(pc -> assertEquals(p1.getColor(), pc));
        assertEquals(1,p4.getCharacterState().getDamageBar().size());
        assertEquals(0, p4.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        assertEquals(tileMap[0][1], p4.getCharacterState().getTile());
        assertEquals(0, p1.getCharacterState().getAmmoBag().get(AmmoColor.RED).intValue());


        p2.getCharacterState().resetDamageBar();
        p2.getCharacterState().resetMarkerBar();
        p3.getCharacterState().resetDamageBar();
        p3.getCharacterState().resetMarkerBar();
        p4.getCharacterState().resetDamageBar();
        p4.getCharacterState().resetMarkerBar();
        p5.getCharacterState().resetDamageBar();
        p5.getCharacterState().resetMarkerBar();
        game.getCurrentActionUnitsList().clear();
    }

    @Test
    public void testFurnace() {
        Weapon weapon = weaponList.stream().filter(w -> w.getName().equals("Furnace")).findFirst().orElse(null);
        System.out.println(weapon.getName());

        game.setCurrentPlayer(p1); // the attacker
        p1.getCharacterState().setTile(tileMap[0][0]);
		p1.getCharacterState().addWeapon(weapon);
        p2.getCharacterState().resetDamageBar();
        p2.getCharacterState().resetMarkerBar();
        p3.getCharacterState().resetDamageBar();
        p3.getCharacterState().resetMarkerBar();
        p4.getCharacterState().resetDamageBar();
        p4.getCharacterState().resetMarkerBar();
        p5.getCharacterState().resetDamageBar();
        p5.getCharacterState().resetMarkerBar();

        // testing the availability of the action units
        assertTrue(weapon.isLoaded());
        assertTrue(weapon.getActionUnitList().stream()
                .map(ActionUnit::getName)
                .collect(Collectors.toList()).containsAll(Arrays.asList("Basic Mode", "Cozy Fire Mode"))
        );
        assertTrue(weapon.getOptionalEffectList().stream()
                .map(ActionUnit::getName)
                .collect(Collectors.toList()).containsAll(Arrays.asList())
        );

        // testing Basic Mode --------------------------------------------------------------------
        System.out.println("Basic Mode --------------------------------------------------------------------");
        ActionUnit basicMode = weapon.getActionUnitList().stream()
                .filter(au -> au.getName().equals("Basic Mode")).findFirst().orElse(null);

        Map<String, List<Targetable>> targetableMap = new HashMap<>();
        List<Targetable> targetableList = new ArrayList<>();

        // 1)
        System.out.println("1) Fail, attacker in room");
        p1.getCharacterState().setTile(tileMap[0][1]);
        targetableList.add(tileMap[0][2]);
        targetableMap.put(CommandConstants.TILELIST, targetableList);
        assertFalse(basicMode.check(game, targetableMap));

        // 2)
        System.out.println("2) Fail, room not visible");
        p1.getCharacterState().setTile(tileMap[0][0]);
        targetableList = new ArrayList<>();
        targetableMap = new HashMap<>();
        targetableList.add(tileMap[1][2]);
        targetableMap.put(CommandConstants.TILELIST, targetableList);
        assertFalse(basicMode.check(game, targetableMap));

        // 2)
        System.out.println("2) System all green");
        p1.getCharacterState().setTile(tileMap[0][0]);
        p2.getCharacterState().setTile(tileMap[0][1]);
        p3.getCharacterState().setTile(tileMap[0][2]);
        p4.getCharacterState().setTile(tileMap[0][3]);
        p5.getCharacterState().setTile(tileMap[1][1]);
        targetableList = new ArrayList<>();
        targetableMap = new HashMap<>();
        targetableList.add(tileMap[0][1]);
        targetableMap.put(CommandConstants.TILELIST, targetableList);
        assertTrue(basicMode.check(game, targetableMap));
        basicMode.run(game,targetableMap);
        p2.getCharacterState().getDamageBar()
                .forEach(pc -> assertEquals(p1.getColor(), pc));
        p3.getCharacterState().getDamageBar()
                .forEach(pc -> assertEquals(p1.getColor(), pc));
        p4.getCharacterState().getDamageBar()
                .forEach(pc -> assertEquals(p1.getColor(), pc));
        assertEquals(1,p2.getCharacterState().getDamageBar().size());
        assertEquals(0, p2.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        assertEquals(1,p3.getCharacterState().getDamageBar().size());
        assertEquals(0, p3.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        assertEquals(1,p4.getCharacterState().getDamageBar().size());
        assertEquals(0, p4.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        assertEquals(0,p5.getCharacterState().getDamageBar().size());
        assertEquals(0, p5.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());

        p2.getCharacterState().resetDamageBar();
        p2.getCharacterState().resetMarkerBar();
        p3.getCharacterState().resetDamageBar();
        p3.getCharacterState().resetMarkerBar();
        p4.getCharacterState().resetDamageBar();
        p4.getCharacterState().resetMarkerBar();
        p5.getCharacterState().resetDamageBar();
        p5.getCharacterState().resetMarkerBar();
        game.getCurrentActionUnitsList().clear();

        // testing Cozy Fire --------------------------------------------------------------------
        System.out.println("Cozy Fire Mode --------------------------------------------------------------------");
        ActionUnit cozyFire = weapon.getActionUnitList().stream()
                .filter(au -> au.getName().equals("Cozy Fire Mode")).findFirst().orElse(null);

        targetableMap = new HashMap<>();
        targetableList = new ArrayList<>();

        // 1)
        System.out.println("1) Fail, is attacker tile");
        targetableList.add(tileMap[0][0]);
        targetableMap.put(CommandConstants.TILELIST, targetableList);
        assertFalse(cozyFire.check(game, targetableMap));

        // 2)
        System.out.println("2) Fail, distance violation");
        targetableMap = new HashMap<>();
        targetableList = new ArrayList<>();
        targetableList.add(tileMap[1][1]);
        targetableMap.put(CommandConstants.TILELIST, targetableList);
        assertFalse(cozyFire.check(game, targetableMap));

        // 2)
        System.out.println("2) System all green");
        targetableMap = new HashMap<>();
        targetableList = new ArrayList<>();
        targetableList.add(tileMap[0][1]);
        p2.getCharacterState().setTile(tileMap[0][1]);
        p3.getCharacterState().setTile(tileMap[0][1]);
        p4.getCharacterState().setTile(tileMap[0][1]);
        p5.getCharacterState().setTile(tileMap[1][1]);
        targetableMap.put(CommandConstants.TILELIST, targetableList);
        assertTrue(cozyFire.check(game, targetableMap));
        cozyFire.run(game,targetableMap);
        p2.getCharacterState().getDamageBar()
                .forEach(pc -> assertEquals(p1.getColor(), pc));
        p3.getCharacterState().getDamageBar()
                .forEach(pc -> assertEquals(p1.getColor(), pc));
        p4.getCharacterState().getDamageBar()
                .forEach(pc -> assertEquals(p1.getColor(), pc));
        assertEquals(1,p2.getCharacterState().getDamageBar().size());
        assertEquals(1, p2.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        assertEquals(1,p3.getCharacterState().getDamageBar().size());
        assertEquals(1, p3.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        assertEquals(1,p4.getCharacterState().getDamageBar().size());
        assertEquals(1, p4.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        assertEquals(0,p5.getCharacterState().getDamageBar().size());
        assertEquals(0, p5.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());


    }

    @Test
    public void testHeatseeker() {
        Weapon weapon = weaponList.stream().filter(w -> w.getName().equals("Heatseeker")).findFirst().orElse(null);
        System.out.println(weapon.getName());

        game.setCurrentPlayer(p1); // the attacker
        p1.getCharacterState().setTile(tileMap[0][0]);
		p1.getCharacterState().addWeapon(weapon);
        p2.getCharacterState().resetDamageBar();
        p2.getCharacterState().resetMarkerBar();
        p3.getCharacterState().resetDamageBar();
        p3.getCharacterState().resetMarkerBar();
        p4.getCharacterState().resetDamageBar();
        p4.getCharacterState().resetMarkerBar();
        p5.getCharacterState().resetDamageBar();
        p5.getCharacterState().resetMarkerBar();

        // testing the availability of the action units
        assertTrue(weapon.isLoaded());
        assertTrue(weapon.getActionUnitList().stream()
                .map(ActionUnit::getName)
                .collect(Collectors.toList()).containsAll(Arrays.asList("Basic Mode"))
        );
        assertTrue(weapon.getOptionalEffectList().stream()
                .map(ActionUnit::getName)
                .collect(Collectors.toList()).containsAll(Arrays.asList())
        );

        // testing Basic Mode --------------------------------------------------------------------
        System.out.println("Basic Mode --------------------------------------------------------------------");
        ActionUnit basicMode = weapon.getActionUnitList().stream()
                .filter(au -> au.getName().equals("Basic Mode")).findFirst().orElse(null);

        Map<String, List<Targetable>> targetableMap = new HashMap<>();
        List<Targetable> targetableList = new ArrayList<>();

        // 1)
        System.out.println("1) Fail, target is visible");
        p2.getCharacterState().setTile(tileMap[1][0]);
        targetableList.add(p2);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);
        assertFalse(basicMode.check(game, targetableMap));

        // 1)
        System.out.println("2) System all green");
        p2.getCharacterState().setTile(tileMap[1][2]);
        targetableList = new ArrayList<>();
        targetableMap = new HashMap<>();
        targetableList.add(p2);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);
        assertTrue(basicMode.check(game, targetableMap));
        basicMode.run(game, targetableMap);
        p2.getCharacterState().getDamageBar()
                .forEach(pc -> assertEquals(p1.getColor(), pc));
        assertEquals(3,p2.getCharacterState().getDamageBar().size());
        assertEquals(0, p2.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());

    }

    @Test
    public void testHellion() {
        Weapon weapon = weaponList.stream().filter(w -> w.getName().equals("Hellion")).findFirst().orElse(null);
        System.out.println(weapon.getName());

        game.setCurrentPlayer(p1); // the attacker
        p1.getCharacterState().setTile(tileMap[0][0]);
		p1.getCharacterState().addWeapon(weapon);
        p2.getCharacterState().resetDamageBar();
        p2.getCharacterState().resetMarkerBar();
        p3.getCharacterState().resetDamageBar();
        p3.getCharacterState().resetMarkerBar();
        p4.getCharacterState().resetDamageBar();
        p4.getCharacterState().resetMarkerBar();
        p5.getCharacterState().resetDamageBar();
        p5.getCharacterState().resetMarkerBar();

        // testing the availability of the action units
        assertTrue(weapon.isLoaded());
        assertTrue(weapon.getActionUnitList().stream()
                .map(ActionUnit::getName)
                .collect(Collectors.toList()).containsAll(Arrays.asList("Basic Mode", "Nano-Tracer Mode"))
        );
        assertTrue(weapon.getOptionalEffectList().stream()
                .map(ActionUnit::getName)
                .collect(Collectors.toList()).containsAll(Arrays.asList())
        );

        // testing Basic Mode --------------------------------------------------------------------
        System.out.println("Basic Mode --------------------------------------------------------------------");
        ActionUnit basicMode = weapon.getActionUnitList().stream()
                .filter(au -> au.getName().equals("Basic Mode")).findFirst().orElse(null);

        Map<String, List<Targetable>> targetableMap = new HashMap<>();
        List<Targetable> targetableList = new ArrayList<>();

        // 1)
        System.out.println("1) Fail, target is not visible");
        p2.getCharacterState().setTile(tileMap[1][2]);
        targetableList.add(p2);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);
        assertFalse(basicMode.check(game, targetableMap));
        // 2)
        System.out.println("2) Fail, target distance violation");
        p2.getCharacterState().setTile(tileMap[0][0]);
        targetableList = new ArrayList<>();
        targetableMap = new HashMap<>();
        targetableList.add(p2);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);
        assertFalse(basicMode.check(game, targetableMap));
        // 2)
        System.out.println("3) System all green");
        p2.getCharacterState().setTile(tileMap[0][1]);
        p3.getCharacterState().setTile(tileMap[0][1]);
        p4.getCharacterState().setTile(tileMap[0][1]);
        p5.getCharacterState().setTile(tileMap[1][1]);
        targetableList = new ArrayList<>();
        targetableMap = new HashMap<>();
        targetableList.add(p2);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);
        assertTrue(basicMode.check(game, targetableMap));
        basicMode.run(game, targetableMap);
        p2.getCharacterState().getDamageBar()
                .forEach(pc -> assertEquals(p1.getColor(), pc));
        p3.getCharacterState().getDamageBar()
                .forEach(pc -> assertEquals(p1.getColor(), pc));
        p4.getCharacterState().getDamageBar()
                .forEach(pc -> assertEquals(p1.getColor(), pc));
        assertEquals(1,p2.getCharacterState().getDamageBar().size());
        assertEquals(1, p2.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        assertEquals(0,p3.getCharacterState().getDamageBar().size());
        assertEquals(1, p3.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        assertEquals(0,p4.getCharacterState().getDamageBar().size());
        assertEquals(1, p4.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        assertEquals(0,p5.getCharacterState().getDamageBar().size());
        assertEquals(0, p5.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        p2.getCharacterState().resetDamageBar();
        p2.getCharacterState().resetMarkerBar();
        p3.getCharacterState().resetDamageBar();
        p3.getCharacterState().resetMarkerBar();
        p4.getCharacterState().resetDamageBar();
        p4.getCharacterState().resetMarkerBar();
        p5.getCharacterState().resetDamageBar();
        p5.getCharacterState().resetMarkerBar();

        // testing Nano-Tracer Mode --------------------------------------------------------------------
        System.out.println("Nano-Tracer Mode --------------------------------------------------------------------");
        ActionUnit nanoTracer = weapon.getActionUnitList().stream()
                .filter(au -> au.getName().equals("Nano-Tracer Mode")).findFirst().orElse(null);

        targetableMap = new HashMap<>();
        targetableList = new ArrayList<>();
        // 1)
        System.out.println("1) Fail, not enough ammo");
        assertFalse(nanoTracer.check(game, targetableMap));
        p1.getCharacterState().getAmmoBag().put(AmmoColor.RED,1);
        // 2)
        System.out.println("2) Fail, target is not visible");
        p2.getCharacterState().setTile(tileMap[1][2]);
        targetableList.add(p2);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);
        assertFalse(nanoTracer.check(game, targetableMap));
        // 3)
        System.out.println("3) Fail, target distance violation");
        p2.getCharacterState().setTile(tileMap[0][0]);
        targetableList = new ArrayList<>();
        targetableMap = new HashMap<>();
        targetableList.add(p2);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);
        assertFalse(nanoTracer.check(game, targetableMap));
        // 4)
        System.out.println("4) System all green");
        p2.getCharacterState().setTile(tileMap[0][1]);
        p3.getCharacterState().setTile(tileMap[0][1]);
        p4.getCharacterState().setTile(tileMap[0][1]);
        p5.getCharacterState().setTile(tileMap[1][1]);
        targetableList = new ArrayList<>();
        targetableMap = new HashMap<>();
        targetableList.add(p2);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);
        assertTrue(nanoTracer.check(game, targetableMap));

        nanoTracer.run(game, targetableMap);
        p2.getCharacterState().getDamageBar()
                .forEach(pc -> assertEquals(p1.getColor(), pc));
        p3.getCharacterState().getDamageBar()
                .forEach(pc -> assertEquals(p1.getColor(), pc));
        p4.getCharacterState().getDamageBar()
                .forEach(pc -> assertEquals(p1.getColor(), pc));
        assertEquals(1,p2.getCharacterState().getDamageBar().size());
        assertEquals(2, p2.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        assertEquals(0,p3.getCharacterState().getDamageBar().size());
        assertEquals(2, p3.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        assertEquals(0,p4.getCharacterState().getDamageBar().size());
        assertEquals(2, p4.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        assertEquals(0,p5.getCharacterState().getDamageBar().size());
        assertEquals(0, p5.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        assertEquals(0, p1.getCharacterState().getAmmoBag().get(AmmoColor.RED).intValue());


    }

    @Test
    public void testFlamethrower() {
        Weapon weapon = weaponList.stream().filter(w -> w.getName().equals("Flamethrower")).findFirst().orElse(null);
        System.out.println(weapon.getName());

        game.setCurrentPlayer(p1); // the attacker
        p1.getCharacterState().setTile(tileMap[0][0]);
		p1.getCharacterState().addWeapon(weapon);
        p2.getCharacterState().resetDamageBar();
        p2.getCharacterState().resetMarkerBar();
        p3.getCharacterState().resetDamageBar();
        p3.getCharacterState().resetMarkerBar();
        p4.getCharacterState().resetDamageBar();
        p4.getCharacterState().resetMarkerBar();
        p5.getCharacterState().resetDamageBar();
        p5.getCharacterState().resetMarkerBar();

        // testing the availability of the action units
        assertTrue(weapon.isLoaded());
        assertTrue(weapon.getActionUnitList().stream()
                .map(ActionUnit::getName)
                .collect(Collectors.toList()).containsAll(Arrays.asList("Basic Mode", "Barbecue Mode"))
        );
        assertTrue(weapon.getOptionalEffectList().stream()
                .map(ActionUnit::getName)
                .collect(Collectors.toList()).containsAll(Arrays.asList())
        );

        // testing Basic Mode --------------------------------------------------------------------
        System.out.println("Basic Mode --------------------------------------------------------------------");
        ActionUnit basicMode = weapon.getActionUnitList().stream()
                .filter(au -> au.getName().equals("Basic Mode")).findFirst().orElse(null);

        Map<String, List<Targetable>> targetableMap = new HashMap<>();
        List<Targetable> targetableList = new ArrayList<>();
        // 1)
        System.out.println("1) Fail, distance violation");
        targetableList.add(tileMap[0][2]);
        targetableMap.put(CommandConstants.TILELIST, targetableList);
        assertFalse(basicMode.check(game, targetableMap));
        // 2)
        System.out.println("2) Fail, not unidirectional tile list");
        targetableList = new ArrayList<>();
        targetableMap = new HashMap<>();
        targetableList.add(tileMap[0][1]);
        targetableList.add(tileMap[1][1]);
        targetableMap.put(CommandConstants. TILELIST, targetableList);
        assertFalse(basicMode.check(game, targetableMap));
        // 3)
        System.out.println("3) Fail, not one tile one target");
        targetableList = new ArrayList<>();
        targetableMap = new HashMap<>();
        targetableList.add(tileMap[0][1]);
        targetableList.add(tileMap[0][2]);
        targetableMap.put(CommandConstants.TILELIST, targetableList);
        targetableList = new ArrayList<>();
        p2.getCharacterState().setTile(tileMap[0][1]);
        p3.getCharacterState().setTile(tileMap[0][3]);
        targetableList.add(p2);
        targetableList.add(p3);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);
        assertFalse(basicMode.check(game, targetableMap));
        // 4)
        System.out.println("4) System all green");
        targetableList = new ArrayList<>();
        targetableMap = new HashMap<>();
        targetableList.add(tileMap[0][2]);
        targetableList.add(tileMap[0][1]);
        targetableMap.put(CommandConstants.TILELIST, targetableList);
        targetableList = new ArrayList<>();
        p2.getCharacterState().setTile(tileMap[0][1]);
        p3.getCharacterState().setTile(tileMap[0][2]);
        targetableList.add(p2);
        targetableList.add(p3);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);
        assertTrue(basicMode.check(game, targetableMap));
        basicMode.run(game, targetableMap);
        assertEquals(1,p2.getCharacterState().getDamageBar().size());
        assertEquals(0,p2.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        assertEquals(1,p3.getCharacterState().getDamageBar().size());
        assertEquals(0,p3.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        p2.getCharacterState().resetDamageBar();
        p2.getCharacterState().resetMarkerBar();
        p3.getCharacterState().resetDamageBar();
        p3.getCharacterState().resetMarkerBar();

        // testing Barbecue Mode --------------------------------------------------------------------
        System.out.println("Barbecue Mode --------------------------------------------------------------------");
        ActionUnit barbecueMode = weapon.getActionUnitList().stream()
                .filter(au -> au.getName().equals("Barbecue Mode")).findFirst().orElse(null);

        // 1)
        System.out.println("1) Fail, not enough ammo");
        assertFalse(barbecueMode.check(game, targetableMap));
        p1.getCharacterState().getAmmoBag().put(AmmoColor.YELLOW,2);
        // 2)
        System.out.println("2) Fail, distance violation");
        targetableMap = new HashMap<>();
        targetableList = new ArrayList<>();
        targetableList.add(tileMap[0][2]);
        targetableMap.put(CommandConstants.TILELIST, targetableList);
        assertFalse(barbecueMode.check(game, targetableMap));
        // 3)
        System.out.println("3) Fail, not unidirectional tile list");
        targetableList = new ArrayList<>();
        targetableMap = new HashMap<>();
        targetableList.add(tileMap[0][1]);
        targetableList.add(tileMap[1][1]);
        targetableMap.put(CommandConstants. TILELIST, targetableList);
        assertFalse(barbecueMode.check(game, targetableMap));
        // 4)
        System.out.println("4) System all green");
        targetableList = new ArrayList<>();
        targetableMap = new HashMap<>();
        targetableList.add(tileMap[0][1]);
        targetableList.add(tileMap[0][2]);
        targetableMap.put(CommandConstants.TILELIST, targetableList);
        p2.getCharacterState().setTile(tileMap[0][1]);
        p3.getCharacterState().setTile(tileMap[0][2]);
        p4.getCharacterState().setTile(tileMap[0][2]);
        p5.getCharacterState().setTile(tileMap[1][2]);
        assertTrue(barbecueMode.check(game, targetableMap));
        barbecueMode.run(game, targetableMap);
        assertEquals(2,p2.getCharacterState().getDamageBar().size());
        assertEquals(0,p2.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        assertEquals(1,p3.getCharacterState().getDamageBar().size());
        assertEquals(0,p3.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        assertEquals(1,p4.getCharacterState().getDamageBar().size());
        assertEquals(0,p4.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        assertEquals(0,p5.getCharacterState().getDamageBar().size());
        assertEquals(0,p5.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        assertEquals(0, p1.getCharacterState().getAmmoBag().get(AmmoColor.YELLOW).intValue());
        p2.getCharacterState().resetDamageBar();
        p2.getCharacterState().resetMarkerBar();
        p3.getCharacterState().resetDamageBar();
        p3.getCharacterState().resetMarkerBar();


    }

    @Test
    public void testGrenadeLauncher() {
        Weapon weapon = weaponList.stream().filter(w -> w.getName().equals("Grenade_Launcher")).findFirst().orElse(null);
        System.out.println(weapon.getName());

        game.setCurrentPlayer(p1); // the attacker
        p1.getCharacterState().setTile(tileMap[0][0]);
		p1.getCharacterState().addWeapon(weapon);
        p2.getCharacterState().resetDamageBar();
        p2.getCharacterState().resetMarkerBar();
        p3.getCharacterState().resetDamageBar();
        p3.getCharacterState().resetMarkerBar();
        p4.getCharacterState().resetDamageBar();
        p4.getCharacterState().resetMarkerBar();
        p5.getCharacterState().resetDamageBar();
        p5.getCharacterState().resetMarkerBar();

        // testing the availability of the action units
        assertTrue(weapon.isLoaded());
        assertTrue(weapon.getActionUnitList().stream()
                .map(ActionUnit::getName)
                .collect(Collectors.toList()).containsAll(Arrays.asList("Basic Mode"))
        );
        assertTrue(weapon.getOptionalEffectList().stream()
                .map(ActionUnit::getName)
                .collect(Collectors.toList()).containsAll(Arrays.asList("Extra Grenade Effect"))
        );

        // testing Basic Mode --------------------------------------------------------------------
        System.out.println("Basic Mode --------------------------------------------------------------------");
        ActionUnit basicMode = weapon.getActionUnitList().stream()
                .filter(au -> au.getName().equals("Basic Mode")).findFirst().orElse(null);

        Map<String, List<Targetable>> targetableMap = new HashMap<>();
        List<Targetable> targetableList = new ArrayList<>();
        // 1)
        System.out.println("1) Fail, target not visible");
        p2.getCharacterState().setTile(tileMap[1][2]);
        targetableList.add(p2);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);
        assertFalse(basicMode.check(game, targetableMap));
        // 2)
        System.out.println("2) Fail, distance violation");
        targetableList = new ArrayList<>();
        targetableMap = new HashMap<>();
        p2.getCharacterState().setTile(tileMap[1][1]);
        targetableList.add(p2);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);
        targetableList = new ArrayList<>();
        targetableList.add(tileMap[1][2]);
        targetableMap.put(CommandConstants.TILELIST, targetableList);
        assertFalse(basicMode.check(game, targetableMap));
        // 3)
        System.out.println("3) System all green");
        targetableList = new ArrayList<>();
        targetableMap = new HashMap<>();
        p2.getCharacterState().setTile(tileMap[1][1]);
        targetableList.add(p2);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);
        targetableList = new ArrayList<>();
        targetableList.add(tileMap[1][0]);
        targetableMap.put(CommandConstants.TILELIST, targetableList);
        assertTrue(basicMode.check(game, targetableMap));
        basicMode.run(game, targetableMap);
        assertEquals(1, p2.getCharacterState().getDamageBar().size());
        assertEquals(0,p2.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        assertEquals(tileMap[1][0], p2.getCharacterState().getTile());

        // testing Extra Grenade Effect --------------------------------------------------------------------
        System.out.println("Extra Grenade Effect --------------------------------------------------------------------");
        ActionUnit extraGrenade = weapon.getOptionalEffectList().stream()
                .filter(au -> au.getName().equals("Extra Grenade Effect")).findFirst().orElseThrow(IllegalStateException::new);

        targetableList = new ArrayList<>();
        targetableMap = new HashMap<>();
        // 1)
        System.out.println("1) Fail, not enough ammo");
        assertFalse(extraGrenade.check(game, targetableMap));
        p1.getCharacterState().getAmmoBag().put(AmmoColor.RED,1);
        // 2)
        System.out.println("2) Fail, tile not visible");
        targetableList.add(tileMap[1][2]);
        targetableMap.put(CommandConstants.TILELIST, targetableList);
        assertFalse(extraGrenade.check(game, targetableMap));
        // 3)
        System.out.println("3) System all green");
        targetableList = new ArrayList<>();
        targetableMap = new HashMap<>();
        p2.getCharacterState().setTile(tileMap[1][0]);
        p3.getCharacterState().setTile(tileMap[1][0]);
        p4.getCharacterState().setTile(tileMap[1][0]);
        p5.getCharacterState().setTile(tileMap[1][2]);
        targetableList.add(tileMap[1][0]);
        targetableMap.put(CommandConstants.TILELIST, targetableList);
        assertTrue(extraGrenade.check(game, targetableMap));
        extraGrenade.run(game ,targetableMap);
        assertEquals(2,p2.getCharacterState().getDamageBar().size());
        assertEquals(0,p2.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        assertEquals(1,p3.getCharacterState().getDamageBar().size());
        assertEquals(0,p3.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        assertEquals(1,p4.getCharacterState().getDamageBar().size());
        assertEquals(0,p4.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        assertEquals(0,p5.getCharacterState().getDamageBar().size());
        assertEquals(0,p5.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        assertEquals(0, p1.getCharacterState().getAmmoBag().get(AmmoColor.RED).intValue());

    }

    @Test
    public void testRocketLauncher() {
        Weapon weapon = weaponList.stream().filter(w -> w.getName().equals("Rocket_Launcher")).findFirst().orElse(null);
        System.out.println(weapon.getName());

        game.setCurrentPlayer(p1); // the attacker
        p1.getCharacterState().setTile(tileMap[0][0]);
		p1.getCharacterState().addWeapon(weapon);
        p2.getCharacterState().resetDamageBar();
        p2.getCharacterState().resetMarkerBar();
        p3.getCharacterState().resetDamageBar();
        p3.getCharacterState().resetMarkerBar();
        p4.getCharacterState().resetDamageBar();
        p4.getCharacterState().resetMarkerBar();
        p5.getCharacterState().resetDamageBar();
        p5.getCharacterState().resetMarkerBar();

        // testing the availability of the action units
        assertTrue(weapon.isLoaded());
        assertTrue(weapon.getActionUnitList().stream()
                .map(ActionUnit::getName)
                .collect(Collectors.toList()).containsAll(Arrays.asList("Basic Mode"))
        );
        assertTrue(weapon.getOptionalEffectList().stream()
                .map(ActionUnit::getName)
                .collect(Collectors.toList()).containsAll(Arrays.asList("Rocket Jump Effect", "Fragmenting Warhead Effect"))
        );

        // testing Basic Mode --------------------------------------------------------------------
        System.out.println("Basic Mode --------------------------------------------------------------------");
        ActionUnit basicMode = weapon.getActionUnitList().stream()
                .filter(au -> au.getName().equals("Basic Mode")).findFirst().orElse(null);

        Map<String, List<Targetable>> targetableMap = new HashMap<>();
        List<Targetable> targetableList = new ArrayList<>();
        // 1)
        System.out.println("1) Fail, target not visible");
        p2.getCharacterState().setTile(tileMap[1][2]);
        targetableList.add(p2);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);
        assertFalse(basicMode.check(game, targetableMap));
        // 2)
        System.out.println("2) Fail, is attacker Tile");
        targetableList = new ArrayList<>();
        targetableMap = new HashMap<>();
        p2.getCharacterState().setTile(tileMap[0][0]);
        targetableList.add(p2);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);
        targetableList = new ArrayList<>();
        targetableList.add(tileMap[1][2]);
        targetableMap.put(CommandConstants.TILELIST, targetableList);
        assertFalse(basicMode.check(game, targetableMap));
        // 3)
        System.out.println("3) Fail, distance violation");
        targetableList = new ArrayList<>();
        targetableMap = new HashMap<>();
        p2.getCharacterState().setTile(tileMap[1][1]);
        targetableList.add(p2);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);
        targetableList = new ArrayList<>();
        targetableList.add(tileMap[1][2]);
        targetableMap.put(CommandConstants.TILELIST, targetableList);
        assertFalse(basicMode.check(game, targetableMap));
        // 4)
        System.out.println("4) System all green");
        targetableList = new ArrayList<>();
        targetableMap = new HashMap<>();
        p2.getCharacterState().setTile(tileMap[1][1]);
        targetableList.add(p2);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);
        targetableList = new ArrayList<>();
        targetableList.add(tileMap[1][0]);
        targetableMap.put(CommandConstants.TILELIST, targetableList);
        assertTrue(basicMode.check(game, targetableMap));
        basicMode.run(game, targetableMap);
        assertEquals(2, p2.getCharacterState().getDamageBar().size());
        assertEquals(0, p2.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        assertEquals(tileMap[1][0], p2.getCharacterState().getTile());


        // testing Rocket Jump Effect --------------------------------------------------------------------
        System.out.println("Testing Rocket Jump Effect ------------------------------------------------------------");
        ActionUnit rocketJump = weapon.getOptionalEffectList().stream()
                .filter(au -> au.getName().equals("Rocket Jump Effect")).findFirst().orElse(null);

        // 1)
        System.out.println("1) Fail, not enough ammo");
        assertFalse(rocketJump.check(game, targetableMap));
        p1.getCharacterState().getAmmoBag().put(AmmoColor.BLUE,1);

        // 2)
        System.out.println("2) Too far");
        targetableList.add(tileMap[1][2]);
        targetableMap.put(CommandConstants.TILELIST, targetableList);
        assertFalse(rocketJump.check(game, targetableMap));
        // 3)
        System.out.println("3) Basic Mode was performed");
        targetableMap = new HashMap<>();
        targetableList = new ArrayList<>();
        targetableList.add(tileMap[1][1]);
        targetableMap.put(CommandConstants.TILELIST, targetableList);
        assertTrue(rocketJump.check(game, targetableMap));
        // 4)
        System.out.println("4) No Basic Mode performed, no targets");
        p2.getCharacterState().setTile(tileMap[1][2]);
        p3.getCharacterState().setTile(tileMap[1][2]);
        p4.getCharacterState().setTile(tileMap[1][2]);
        p5.getCharacterState().setTile(tileMap[1][2]);
        game.getCurrentActionUnitsList().remove(basicMode); // <-- no basic mode, needs to check CanShoot
        assertFalse(rocketJump.check(game, targetableMap));
        p2.getCharacterState().setTile(tileMap[1][0]);
        // 5)
        System.out.println("5) System all green");
        assertTrue(rocketJump.check(game,targetableMap));
        rocketJump.run(game, targetableMap);
        p2.getCharacterState().getDamageBar()
                .forEach(pc -> assertEquals(p1.getColor(), pc));
        assertEquals(2,p2.getCharacterState().getDamageBar().size());
        assertEquals(0, p2.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        assertEquals(0, p1.getCharacterState().getAmmoBag().get(AmmoColor.BLUE).intValue());
        assertTrue(p1.getCharacterState().getTile().equals(tileMap[1][1]));

        targetableMap = new HashMap<>();
        targetableList = new ArrayList<>();

        // testing Fragmenting Warhead Effect --------------------------------------------------------------------
        System.out.println("Testing Fragmenting Warhead Effect ------------------------------------------------------------");
        ActionUnit fragmentingWarhead = weapon.getOptionalEffectList().stream()
                .filter(au -> au.getName().equals("Fragmenting Warhead Effect")).findFirst().orElse(null);

        // 1)
        System.out.println("1) Fail, not enough ammo");
        assertFalse(fragmentingWarhead.check(game, targetableMap));
        p1.getCharacterState().getAmmoBag().put(AmmoColor.YELLOW,1);

        // 2)
        System.out.println("2) Fail, missing Basic Mode");
        game.getCurrentActionUnitsList().remove(basicMode);
        assertFalse(fragmentingWarhead.check(game, targetableMap));
        game.getCurrentActionUnitsList().add(basicMode);
        // 3)
        System.out.println("3) System all green");
        p3.getCharacterState().setTile(tileMap[1][1]);
        p4.getCharacterState().setTile(tileMap[1][1]);
        p5.getCharacterState().setTile(tileMap[1][2]);
        assertTrue(fragmentingWarhead.check(game, targetableMap));
        fragmentingWarhead.run(game, targetableMap);
        assertEquals(0,p1.getCharacterState().getDamageBar().size());
        assertEquals(0,p1.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        assertEquals(3,p2.getCharacterState().getDamageBar().size());
        assertEquals(0,p2.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        assertEquals(1,p3.getCharacterState().getDamageBar().size());
        assertEquals(0,p3.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        assertEquals(1,p4.getCharacterState().getDamageBar().size());
        assertEquals(0,p4.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        assertEquals(0,p5.getCharacterState().getDamageBar().size());
        assertEquals(0,p5.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        assertEquals(0, p1.getCharacterState().getAmmoBag().get(AmmoColor.YELLOW).intValue());
    }

    @Test
    public void testRailgun() {
        Weapon weapon = weaponList.stream().filter(w -> w.getName().equals("Railgun")).findFirst().orElse(null);
        System.out.println(weapon.getName());

        game.setCurrentPlayer(p1); // the attacker
        p1.getCharacterState().setTile(tileMap[0][0]);
		p1.getCharacterState().addWeapon(weapon);
        p2.getCharacterState().resetDamageBar();
        p2.getCharacterState().resetMarkerBar();
        p3.getCharacterState().resetDamageBar();
        p3.getCharacterState().resetMarkerBar();
        p4.getCharacterState().resetDamageBar();
        p4.getCharacterState().resetMarkerBar();
        p5.getCharacterState().resetDamageBar();
        p5.getCharacterState().resetMarkerBar();

        // testing the availability of the action units
        assertTrue(weapon.isLoaded());
        assertTrue(weapon.getActionUnitList().stream()
                .map(ActionUnit::getName)
                .collect(Collectors.toList()).containsAll(Arrays.asList("Basic Mode", "Piercing Mode"))
        );
        assertTrue(weapon.getOptionalEffectList().stream()
                .map(ActionUnit::getName)
                .collect(Collectors.toList()).containsAll(Arrays.asList())
        );

        // testing Basic Mode --------------------------------------------------------------------
        System.out.println("Basic Mode --------------------------------------------------------------------");
        ActionUnit basicMode = weapon.getActionUnitList().stream()
                .filter(au -> au.getName().equals("Basic Mode")).findFirst().orElse(null);

        Map<String, List<Targetable>> targetableMap;
        List<Targetable> targetableList;

        // 1)
        System.out.println("1) Fail, illegal direction");
        targetableMap = new HashMap<>();
        targetableList = new ArrayList<>();
        targetableList.add(tileMap[1][2]);
        targetableMap.put(CommandConstants.TILELIST, targetableList);
        targetableList = new ArrayList<>();
        p2.getCharacterState().setTile(tileMap[0][1]);
        targetableList.add(p2);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);
        assertFalse(basicMode.check(game, targetableMap));
        // 2)
        System.out.println("2) Fail, targets not in direction");
        targetableMap = new HashMap<>();
        targetableList = new ArrayList<>();
        targetableList.add(tileMap[1][0]);
        targetableMap.put(CommandConstants.TILELIST, targetableList);
        targetableList = new ArrayList<>();
        p2.getCharacterState().setTile(tileMap[0][1]);
        targetableList.add(p2);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);
        assertFalse(basicMode.check(game, targetableMap));
        // 3)
        System.out.println("3) System all green");
        targetableMap = new HashMap<>();
        targetableList = new ArrayList<>();
        targetableList.add(tileMap[0][1]);
        targetableMap.put(CommandConstants.TILELIST, targetableList);
        targetableList = new ArrayList<>();
        p2.getCharacterState().setTile(tileMap[0][3]);
        targetableList.add(p2);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);
        assertTrue(basicMode.check(game, targetableMap));
        basicMode.run(game, targetableMap);
        assertEquals(3,p2.getCharacterState().getDamageBar().size());
        assertEquals(0,p2.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        p2.getCharacterState().resetDamageBar();
        p2.getCharacterState().resetMarkerBar();

        // testing Piercing Mode --------------------------------------------------------------------
        System.out.println("Piercing Mode --------------------------------------------------------------------");
        ActionUnit piercingMode = weapon.getActionUnitList().stream()
                .filter(au -> au.getName().equals("Piercing Mode")).findFirst().orElse(null);

        // 1)
        System.out.println("1) Fail, illegal direction");
        targetableMap = new HashMap<>();
        targetableList = new ArrayList<>();
        targetableList.add(tileMap[1][2]);
        targetableMap.put(CommandConstants.TILELIST, targetableList);
        targetableList = new ArrayList<>();
        p2.getCharacterState().setTile(tileMap[0][1]);
        targetableList.add(p2);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);
        assertFalse(piercingMode.check(game, targetableMap));
        // 2)
        System.out.println("2) Fail, targets not in direction");
        targetableMap = new HashMap<>();
        targetableList = new ArrayList<>();
        targetableList.add(tileMap[1][0]);
        targetableMap.put(CommandConstants.TILELIST, targetableList);
        targetableList = new ArrayList<>();
        p2.getCharacterState().setTile(tileMap[0][1]);
        targetableList.add(p2);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);
        assertFalse(piercingMode.check(game, targetableMap));
        // 3)
        System.out.println("3) System all green");
        targetableMap = new HashMap<>();
        targetableList = new ArrayList<>();
        targetableList.add(tileMap[0][1]);
        targetableMap.put(CommandConstants.TILELIST, targetableList);
        targetableList = new ArrayList<>();
        p2.getCharacterState().setTile(tileMap[0][3]);
        p3.getCharacterState().setTile(tileMap[0][2]);
        targetableList.add(p2);
        targetableList.add(p3);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);
        assertTrue(piercingMode.check(game, targetableMap));
        piercingMode.run(game, targetableMap);
        assertEquals(2,p2.getCharacterState().getDamageBar().size());
        assertEquals(0,p2.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        assertEquals(2,p3.getCharacterState().getDamageBar().size());
        assertEquals(0,p3.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
    }

    @Test
    public void testCyberblade() {
        Weapon weapon = weaponList.stream().filter(w -> w.getName().equals("Cyberblade")).findFirst().orElse(null);
        System.out.println(weapon.getName());

        game.setCurrentPlayer(p1); // the attacker
        p1.getCharacterState().setTile(tileMap[0][0]);
		p1.getCharacterState().addWeapon(weapon);
        p2.getCharacterState().resetDamageBar();
        p2.getCharacterState().resetMarkerBar();
        p3.getCharacterState().resetDamageBar();
        p3.getCharacterState().resetMarkerBar();
        p4.getCharacterState().resetDamageBar();
        p4.getCharacterState().resetMarkerBar();
        p5.getCharacterState().resetDamageBar();
        p5.getCharacterState().resetMarkerBar();

        // testing the availability of the action units
        assertTrue(weapon.isLoaded());
        assertTrue(weapon.getActionUnitList().stream()
                .map(ActionUnit::getName)
                .collect(Collectors.toList()).containsAll(Arrays.asList("Basic Mode"))
        );
        assertTrue(weapon.getOptionalEffectList().stream()
                .map(ActionUnit::getName)
                .collect(Collectors.toList()).containsAll(Arrays.asList("Shadowstep Effect", "Slice and Dice Effect"))
        );

        // testing Basic Mode --------------------------------------------------------------------
        System.out.println("Basic Mode --------------------------------------------------------------------");
        ActionUnit basicMode = weapon.getActionUnitList().stream()
                .filter(au -> au.getName().equals("Basic Mode")).findFirst().orElse(null);

        Map<String, List<Targetable>> targetableMap;
        List<Targetable> targetableList;

        // 1)
        System.out.println("1) Fail, not attacker tile");
        targetableMap = new HashMap<>();
        targetableList = new ArrayList<>();
        p2.getCharacterState().setTile(tileMap[0][1]);
        targetableList.add(p2);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);
        assertFalse(basicMode.check(game, targetableMap));
        // 2)
        System.out.println("2) System all green");
        targetableMap = new HashMap<>();
        targetableList = new ArrayList<>();
        p2.getCharacterState().setTile(tileMap[0][0]);
        targetableList.add(p2);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);
        assertTrue(basicMode.check(game, targetableMap));
        basicMode.run(game, targetableMap);
        assertEquals(2,p2.getCharacterState().getDamageBar().size());
        assertEquals(0,p2.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());

        // testing Shadowstep Effect --------------------------------------------------------------------
        System.out.println("Testing Shadowstep Effect ------------------------------------------------------------");
        ActionUnit shadowstep = weapon.getOptionalEffectList().stream()
                .filter(au -> au.getName().equals("Shadowstep Effect")).findFirst().orElse(null);

        // 1)
        System.out.println("1) Too far");
        targetableMap = new HashMap<>();
        targetableList = new ArrayList<>();
        targetableList.add(tileMap[1][2]);
        targetableMap.put(CommandConstants.TILELIST, targetableList);
        assertFalse(shadowstep.check(game, targetableMap));
        // 2)
        System.out.println("2) Basic Mode was performed");
        targetableMap = new HashMap<>();
        targetableList = new ArrayList<>();
        targetableList.add(tileMap[0][1]);
        targetableMap.put(CommandConstants.TILELIST, targetableList);
        assertTrue(shadowstep.check(game, targetableMap));
        // 3)
        System.out.println("3) No Basic Mode performed, no targets");
        p2.getCharacterState().setTile(tileMap[1][2]);
        p3.getCharacterState().setTile(tileMap[1][2]);
        p4.getCharacterState().setTile(tileMap[1][2]);
        p5.getCharacterState().setTile(tileMap[1][2]);
        game.getCurrentActionUnitsList().remove(basicMode); // <-- no basic mode, needs to check CanShoot
        assertFalse(shadowstep.check(game, targetableMap));
        // 4)
        System.out.println("4) System all green");
        p2.getCharacterState().setTile(tileMap[1][0]);
        assertTrue(shadowstep.check(game,targetableMap));
        shadowstep.run(game, targetableMap);
        p2.getCharacterState().getDamageBar()
                .forEach(pc -> assertEquals(p1.getColor(), pc));
        assertEquals(2,p2.getCharacterState().getDamageBar().size());
        assertEquals(0, p2.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        assertTrue(p1.getCharacterState().getTile().equals(tileMap[0][1]));

        targetableMap = new HashMap<>();
        targetableList = new ArrayList<>();

        // testing Shadowstep Effect --------------------------------------------------------------------
        System.out.println("Testing Slice and Dice Effect ------------------------------------------------------------");
        ActionUnit sliceAndDice = weapon.getOptionalEffectList().stream()
                .filter(au -> au.getName().equals("Slice and Dice Effect")).findFirst().orElse(null);

        // 1)
        System.out.println("1) Fail, not enough ammo");
        assertFalse(sliceAndDice.check(game, targetableMap));
        p1.getCharacterState().getAmmoBag().put(AmmoColor.YELLOW,1);
        // 2)
        System.out.println("2) Fail, missing Basic Mode");
        game.getCurrentActionUnitsList().remove(basicMode);
        assertFalse(sliceAndDice.check(game, targetableMap));
        game.getCurrentActionUnitsList().add(basicMode);
        // 3)
        System.out.println("3) Fail, not attacker tile");
        targetableMap = new HashMap<>();
        targetableList = new ArrayList<>();
        p2.getCharacterState().setTile(tileMap[0][2]); //<-- p1 at 0,1
        targetableList.add(p2);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);
        assertFalse(sliceAndDice.check(game, targetableMap));
        // 4)
        System.out.println("4) Fail, target in basic mode");
        targetableMap = new HashMap<>();
        targetableList = new ArrayList<>();
        p2.getCharacterState().setTile(tileMap[0][1]);
        targetableList.add(p2);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);
        assertFalse(sliceAndDice.check(game, targetableMap));
        // 5)
        System.out.println("5) System all green");
        targetableMap = new HashMap<>();
        targetableList = new ArrayList<>();
        p3.getCharacterState().setTile(tileMap[0][1]);
        targetableList.add(p3);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);
        assertTrue(sliceAndDice.check(game, targetableMap));
        sliceAndDice.run(game, targetableMap);
        assertEquals(2, p2.getCharacterState().getDamageBar().size());
        assertEquals(2, p3.getCharacterState().getDamageBar().size());

    }

    @Test
    public void testZX2() {
        Weapon weapon = weaponList.stream().filter(w -> w.getName().equals("ZX-2")).findFirst().orElse(null);
        System.out.println(weapon.getName());

        game.setCurrentPlayer(p1); // the attacker
        p1.getCharacterState().setTile(tileMap[0][0]);
		p1.getCharacterState().addWeapon(weapon);
        p2.getCharacterState().resetDamageBar();
        p2.getCharacterState().resetMarkerBar();
        p3.getCharacterState().resetDamageBar();
        p3.getCharacterState().resetMarkerBar();
        p4.getCharacterState().resetDamageBar();
        p4.getCharacterState().resetMarkerBar();
        p5.getCharacterState().resetDamageBar();
        p5.getCharacterState().resetMarkerBar();

        // testing the availability of the action units
        assertTrue(weapon.isLoaded());
        assertTrue(weapon.getActionUnitList().stream()
                .map(ActionUnit::getName)
                .collect(Collectors.toList()).containsAll(Arrays.asList("Basic Mode", "Scanner Mode"))
        );
        assertTrue(weapon.getOptionalEffectList().stream()
                .map(ActionUnit::getName)
                .collect(Collectors.toList()).containsAll(Arrays.asList())
        );

        // testing Basic Mode --------------------------------------------------------------------
        System.out.println("Basic Mode --------------------------------------------------------------------");
        ActionUnit basicMode = weapon.getActionUnitList().stream()
                .filter(au -> au.getName().equals("Basic Mode")).findFirst().orElse(null);

        Map<String, List<Targetable>> targetableMap;
        List<Targetable> targetableList;

        // 1)
        System.out.println("1) Fail, target not visible");
        targetableMap = new HashMap<>();
        targetableList = new ArrayList<>();
        p2.getCharacterState().setTile(tileMap[1][2]);
        targetableList.add(p2);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);
        assertFalse(basicMode.check(game, targetableMap));
        // 2)
        System.out.println("2) System all green");
        targetableMap = new HashMap<>();
        targetableList = new ArrayList<>();
        p2.getCharacterState().setTile(tileMap[1][0]);
        targetableList.add(p2);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);
        assertTrue(basicMode.check(game, targetableMap));
        basicMode.run(game, targetableMap);
        assertEquals(1, p2.getCharacterState().getDamageBar().size());
        assertEquals(2, p2.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        p2.getCharacterState().resetDamageBar();
        p2.getCharacterState().resetMarkerBar();

        // testing Scanner Mode --------------------------------------------------------------------
        System.out.println("Scanner Mode --------------------------------------------------------------------");
        ActionUnit scanner = weapon.getActionUnitList().stream()
                .filter(au -> au.getName().equals("Scanner Mode")).findFirst().orElse(null);

        // 1)
        System.out.println("1) Fail, target not visible");
        targetableMap = new HashMap<>();
        targetableList = new ArrayList<>();
        p2.getCharacterState().setTile(tileMap[1][2]);
        targetableList.add(p2);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);
        assertFalse(scanner.check(game, targetableMap));
        // 1)
        System.out.println("1) Fail, one target was duplicated");
        targetableMap = new HashMap<>();
        targetableList = new ArrayList<>();
        p2.getCharacterState().setTile(tileMap[1][0]);
        p3.getCharacterState().setTile(tileMap[0][1]);
        targetableList.add(p2);
        targetableList.add(p3);
        targetableList.add(p3);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);
        assertFalse(scanner.check(game, targetableMap));
        // 3)
        System.out.println("3) System all green");
        targetableMap = new HashMap<>();
        targetableList = new ArrayList<>();
        p2.getCharacterState().setTile(tileMap[1][0]);
        p3.getCharacterState().setTile(tileMap[0][1]);
        p4.getCharacterState().setTile(tileMap[0][1]);
        targetableList.add(p2);
        targetableList.add(p3);
        targetableList.add(p4);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);
        assertTrue(scanner.check(game, targetableMap));
        scanner.run(game, targetableMap);
        assertEquals(0, p2.getCharacterState().getDamageBar().size());
        assertEquals(1, p2.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        assertEquals(1, p3.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        assertEquals(1, p4.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());




    }

    @Test
    public void testShotgun() {
        Weapon weapon = weaponList.stream().filter(w -> w.getName().equals("Shotgun")).findFirst().orElse(null);
        System.out.println(weapon.getName());

        game.setCurrentPlayer(p1); // the attacker
        p1.getCharacterState().setTile(tileMap[0][0]);
		p1.getCharacterState().addWeapon(weapon);
        p2.getCharacterState().resetDamageBar();
        p2.getCharacterState().resetMarkerBar();
        p3.getCharacterState().resetDamageBar();
        p3.getCharacterState().resetMarkerBar();
        p4.getCharacterState().resetDamageBar();
        p4.getCharacterState().resetMarkerBar();
        p5.getCharacterState().resetDamageBar();
        p5.getCharacterState().resetMarkerBar();

        // testing the availability of the action units
        assertTrue(weapon.isLoaded());
        assertTrue(weapon.getActionUnitList().stream()
                .map(ActionUnit::getName)
                .collect(Collectors.toList()).containsAll(Arrays.asList("Basic Mode", "Long Barrel Mode"))
        );
        assertTrue(weapon.getOptionalEffectList().stream()
                .map(ActionUnit::getName)
                .collect(Collectors.toList()).containsAll(Arrays.asList())
        );

        // testing Basic Mode --------------------------------------------------------------------
        System.out.println("Basic Mode --------------------------------------------------------------------");
        ActionUnit basicMode = weapon.getActionUnitList().stream()
                .filter(au -> au.getName().equals("Basic Mode")).findFirst().orElse(null);

        Map<String, List<Targetable>> targetableMap;
        List<Targetable> targetableList;

        // 1)
        System.out.println("1) Fail, not attacker tile");
        targetableMap = new HashMap<>();
        targetableList = new ArrayList<>();
        p2.getCharacterState().setTile(tileMap[0][1]);
        targetableList.add(p2);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);
        assertFalse(basicMode.check(game, targetableMap));
        // 2)
        System.out.println("2) Fail, distance violation");
        targetableList = new ArrayList<>();
        targetableMap = new HashMap<>();
        p2.getCharacterState().setTile(tileMap[0][0]);
        targetableList.add(p2);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);
        targetableList = new ArrayList<>();
        targetableList.add(tileMap[1][1]);
        targetableMap.put(CommandConstants.TILELIST, targetableList);
        assertFalse(basicMode.check(game, targetableMap));
        // 3)
        System.out.println("3) System all green");
        targetableList = new ArrayList<>();
        targetableMap = new HashMap<>();
        p2.getCharacterState().setTile(tileMap[0][0]);
        targetableList.add(p2);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);
        targetableList = new ArrayList<>();
        targetableList.add(tileMap[1][0]);
        targetableMap.put(CommandConstants.TILELIST, targetableList);
        assertTrue(basicMode.check(game, targetableMap));
        basicMode.run(game, targetableMap);
        assertEquals(3, p2.getCharacterState().getDamageBar().size());
        assertEquals(tileMap[1][0], p2.getCharacterState().getTile());
        p2.getCharacterState().resetDamageBar();
        p2.getCharacterState().resetMarkerBar();

        // testing Long Barrel Mode --------------------------------------------------------------------
        System.out.println("Long Barrel Mode --------------------------------------------------------------------");
        ActionUnit longBarrel = weapon.getActionUnitList().stream()
                .filter(au -> au.getName().equals("Long Barrel Mode")).findFirst().orElse(null);

        // 1)
        System.out.println("1) Fail, distance violated");
        targetableList = new ArrayList<>();
        targetableMap = new HashMap<>();
        p2.getCharacterState().setTile(tileMap[1][2]);
        targetableList.add(p2);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);
        assertFalse(longBarrel.check(game, targetableMap));
        // 2)
        System.out.println("2) System all green");
        targetableList = new ArrayList<>();
        targetableMap = new HashMap<>();
        p2.getCharacterState().setTile(tileMap[1][0]);
        targetableList.add(p2);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);
        assertTrue(longBarrel.check(game, targetableMap));
        longBarrel.run(game, targetableMap);
        assertEquals(2, p2.getCharacterState().getDamageBar().size());

    }

    @Test
    public void testPowerGlove() {
        Weapon weapon = weaponList.stream().filter(w -> w.getName().equals("Power_Glove")).findFirst().orElse(null);
        System.out.println(weapon.getName());

        game.setCurrentPlayer(p1); // the attacker
        p1.getCharacterState().setTile(tileMap[0][0]);
		p1.getCharacterState().addWeapon(weapon);
        p2.getCharacterState().resetDamageBar();
        p2.getCharacterState().resetMarkerBar();
        p3.getCharacterState().resetDamageBar();
        p3.getCharacterState().resetMarkerBar();
        p4.getCharacterState().resetDamageBar();
        p4.getCharacterState().resetMarkerBar();
        p5.getCharacterState().resetDamageBar();
        p5.getCharacterState().resetMarkerBar();

        // testing the availability of the action units
        assertTrue(weapon.isLoaded());
        assertTrue(weapon.getActionUnitList().stream()
                .map(ActionUnit::getName)
                .collect(Collectors.toList()).containsAll(Arrays.asList("Basic Mode", "Rocket Fist Mode"))
        );
        assertTrue(weapon.getOptionalEffectList().stream()
                .map(ActionUnit::getName)
                .collect(Collectors.toList()).containsAll(Arrays.asList())
        );

        // testing Basic Mode --------------------------------------------------------------------
        System.out.println("Basic Mode --------------------------------------------------------------------");
        ActionUnit basicMode = weapon.getActionUnitList().stream()
                .filter(au -> au.getName().equals("Basic Mode")).findFirst().orElse(null);

        Map<String, List<Targetable>> targetableMap;
        List<Targetable> targetableList;

        // 1)
        System.out.println("1) Fail, distance violation");
        targetableList = new ArrayList<>();
        targetableMap = new HashMap<>();
        p2.getCharacterState().setTile(tileMap[1][2]);
        targetableList.add(p2);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);
        assertFalse(basicMode.check(game, targetableMap));
        // 2)
        System.out.println("2) System all green");
        targetableList = new ArrayList<>();
        targetableMap = new HashMap<>();
        p2.getCharacterState().setTile(tileMap[1][0]);
        targetableList.add(p2);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);
        assertTrue(basicMode.check(game, targetableMap));
        basicMode.run(game, targetableMap);
        assertEquals(1, p2.getCharacterState().getDamageBar().size());
        assertEquals(2, p2.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        assertEquals(tileMap[1][0], p1.getCharacterState().getTile());
        p2.getCharacterState().resetDamageBar();
        p2.getCharacterState().resetMarkerBar();

        // testing Basic Mode --------------------------------------------------------------------
        System.out.println("Rocket Fist Mode --------------------------------------------------------------------");
        ActionUnit rocketFist = weapon.getActionUnitList().stream()
                .filter(au -> au.getName().equals("Rocket Fist Mode")).findFirst().orElse(null);

        p1.getCharacterState().setTile(tileMap[0][0]);
        // 1)
        System.out.println("1) Fail, not enough ammo");
        assertFalse(rocketFist.check(game, targetableMap));
        p1.getCharacterState().getAmmoBag().put(AmmoColor.BLUE,1);
        // 2)
        System.out.println("2) Fail, distance violation");
        targetableMap = new HashMap<>();
        targetableList = new ArrayList<>();
        targetableList.add(tileMap[0][2]);
        targetableMap.put(CommandConstants.TILELIST, targetableList);
        assertFalse(rocketFist.check(game, targetableMap));
        // 3)
        System.out.println("3) Fail, not unidirectional tile list");
        targetableList = new ArrayList<>();
        targetableMap = new HashMap<>();
        targetableList.add(tileMap[0][1]);
        targetableList.add(tileMap[1][1]);
        targetableMap.put(CommandConstants. TILELIST, targetableList);
        assertFalse(rocketFist.check(game, targetableMap));
        // 4)
        System.out.println("4) Fail, not one tile one target");
        targetableList = new ArrayList<>();
        targetableMap = new HashMap<>();
        targetableList.add(tileMap[0][1]);
        targetableList.add(tileMap[0][2]);
        targetableMap.put(CommandConstants.TILELIST, targetableList);
        targetableList = new ArrayList<>();
        p2.getCharacterState().setTile(tileMap[0][1]);
        p3.getCharacterState().setTile(tileMap[0][3]);
        targetableList.add(p2);
        targetableList.add(p3);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);
        assertFalse(rocketFist.check(game, targetableMap));
        // 5)
        System.out.println("5) System all green");
        targetableList = new ArrayList<>();
        targetableMap = new HashMap<>();
        targetableList.add(tileMap[0][1]);
        targetableList.add(tileMap[0][2]);
        targetableMap.put(CommandConstants.TILELIST, targetableList);
        targetableList = new ArrayList<>();
        p2.getCharacterState().setTile(tileMap[0][1]);
        p3.getCharacterState().setTile(tileMap[0][2]);
        targetableList.add(p2);
        targetableList.add(p3);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);
        assertTrue(rocketFist.check(game, targetableMap));
        rocketFist.run(game, targetableMap);
        assertEquals(2,p2.getCharacterState().getDamageBar().size());
        assertEquals(0,p2.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        assertEquals(2,p3.getCharacterState().getDamageBar().size());
        assertEquals(0,p3.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        p2.getCharacterState().resetDamageBar();
        p2.getCharacterState().resetMarkerBar();
        p3.getCharacterState().resetDamageBar();
        p3.getCharacterState().resetMarkerBar();

        // 6)
        System.out.println("6) System all green, 2nd option only one target 2 moves");
        targetableList = new ArrayList<>();
        targetableMap = new HashMap<>();
        targetableList.add(tileMap[0][1]);
        targetableList.add(tileMap[0][2]);
        targetableMap.put(CommandConstants.TILELIST, targetableList);
        targetableList = new ArrayList<>();
        p2.getCharacterState().setTile(tileMap[0][1]);
        targetableList.add(p2);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);
        assertTrue(rocketFist.check(game, targetableMap));
        rocketFist.run(game, targetableMap);
        assertEquals(2,p2.getCharacterState().getDamageBar().size());
        assertEquals(0,p2.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        assertEquals(0,p3.getCharacterState().getDamageBar().size());
        assertEquals(0,p3.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        p2.getCharacterState().resetDamageBar();
        p2.getCharacterState().resetMarkerBar();
        p3.getCharacterState().resetDamageBar();
        p3.getCharacterState().resetMarkerBar();

        // 7)
        System.out.println("7) Fail, one tile 2 targets");
        targetableList = new ArrayList<>();
        targetableMap = new HashMap<>();
        targetableList.add(tileMap[0][1]);
        targetableMap.put(CommandConstants.TILELIST, targetableList);
        targetableList = new ArrayList<>();
        p2.getCharacterState().setTile(tileMap[0][1]);
        p3.getCharacterState().setTile(tileMap[0][2]);
        targetableList.add(p2);
        targetableList.add(p3);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);
        assertFalse(rocketFist.check(game, targetableMap));

    }

    @Test
    public void testShockwave() {
        Weapon weapon = weaponList.stream().filter(w -> w.getName().equals("Shockwave")).findFirst().orElse(null);
        System.out.println(weapon.getName());

        game.setCurrentPlayer(p1); // the attacker
        p1.getCharacterState().setTile(tileMap[0][0]);
		p1.getCharacterState().addWeapon(weapon);
        p2.getCharacterState().resetDamageBar();
        p2.getCharacterState().resetMarkerBar();
        p3.getCharacterState().resetDamageBar();
        p3.getCharacterState().resetMarkerBar();
        p4.getCharacterState().resetDamageBar();
        p4.getCharacterState().resetMarkerBar();
        p5.getCharacterState().resetDamageBar();
        p5.getCharacterState().resetMarkerBar();

        // testing the availability of the action units
        assertTrue(weapon.isLoaded());
        assertTrue(weapon.getActionUnitList().stream()
                .map(ActionUnit::getName)
                .collect(Collectors.toList()).containsAll(Arrays.asList("Basic Mode", "Tsunami Mode"))
        );
        assertTrue(weapon.getOptionalEffectList().stream()
                .map(ActionUnit::getName)
                .collect(Collectors.toList()).containsAll(Arrays.asList())
        );

        // testing Basic Mode --------------------------------------------------------------------
        System.out.println("Basic Mode --------------------------------------------------------------------");
        ActionUnit basicMode = weapon.getActionUnitList().stream()
                .filter(au -> au.getName().equals("Basic Mode")).findFirst().orElse(null);

        Map<String, List<Targetable>> targetableMap;
        List<Targetable> targetableList;

        // 1)
        System.out.println("1) Fail, distance violation");
        targetableList = new ArrayList<>();
        targetableMap = new HashMap<>();
        p2.getCharacterState().setTile(tileMap[1][0]); // is false, even with one target in correct distance
        p3.getCharacterState().setTile(tileMap[1][2]);
        p4.getCharacterState().setTile(tileMap[1][2]);
        targetableList.add(p2);
        targetableList.add(p3);
        targetableList.add(p4);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);
        assertFalse(basicMode.check(game, targetableMap));
        // 2)
        System.out.println("2) Fail, target not in different tiles");
        targetableList = new ArrayList<>();
        p1.getCharacterState().setTile(tileMap[0][2]);
        p2.getCharacterState().setTile(tileMap[0][1]);
        p3.getCharacterState().setTile(tileMap[0][3]);
        p4.getCharacterState().setTile(tileMap[0][3]);
        targetableList.add(p2);
        targetableList.add(p3);
        targetableList.add(p4);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);
        assertFalse(basicMode.check(game, targetableMap));
        // 3)
        System.out.println("3) System all green");
        targetableList = new ArrayList<>();
        p1.getCharacterState().setTile(tileMap[0][2]);
        p2.getCharacterState().setTile(tileMap[0][1]);
        p3.getCharacterState().setTile(tileMap[0][3]);
        p4.getCharacterState().setTile(tileMap[1][2]);
        targetableList.add(p2);
        targetableList.add(p3);
        targetableList.add(p4);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);
        assertTrue(basicMode.check(game, targetableMap));
        basicMode.run(game, targetableMap);
        assertEquals(1,p2.getCharacterState().getDamageBar().size());
        assertEquals(0,p2.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        assertEquals(1,p3.getCharacterState().getDamageBar().size());
        assertEquals(0,p3.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        assertEquals(1,p4.getCharacterState().getDamageBar().size());
        assertEquals(0,p4.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        p2.getCharacterState().resetDamageBar();
        p2.getCharacterState().resetMarkerBar();
        p3.getCharacterState().resetDamageBar();
        p3.getCharacterState().resetMarkerBar();
        p4.getCharacterState().resetDamageBar();
        p4.getCharacterState().resetMarkerBar();

        // testing Basic Mode --------------------------------------------------------------------
        System.out.println("Tsunami Mode --------------------------------------------------------------------");
        ActionUnit tsunamiMode = weapon.getActionUnitList().stream()
                .filter(au -> au.getName().equals("Tsunami Mode")).findFirst().orElse(null);


        // 1)
        System.out.println("1) Fail, not enough ammo");
        assertFalse(tsunamiMode.check(game, targetableMap));
        p1.getCharacterState().getAmmoBag().put(AmmoColor.YELLOW,1);

        // 2)
        System.out.println("2) Fail, no target at given distance");
        p1.getCharacterState().setTile(tileMap[0][0]);
        p2.getCharacterState().setTile(tileMap[1][2]);
        p3.getCharacterState().setTile(tileMap[1][2]);
        p4.getCharacterState().setTile(tileMap[1][2]);
        p5.getCharacterState().setTile(tileMap[1][2]);
        assertFalse(tsunamiMode.check(game, targetableMap));

        // 3)
        System.out.println("3) System all green");
        p1.getCharacterState().setTile(tileMap[0][2]);
        p2.getCharacterState().setTile(tileMap[0][1]);
        p3.getCharacterState().setTile(tileMap[0][3]);
        p4.getCharacterState().setTile(tileMap[1][2]);
        p5.getCharacterState().setTile(tileMap[1][2]);
        assertTrue(tsunamiMode.check(game, targetableMap));
        tsunamiMode.run(game, targetableMap);
        assertEquals(1,p2.getCharacterState().getDamageBar().size());
        assertEquals(0,p2.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        assertEquals(1,p3.getCharacterState().getDamageBar().size());
        assertEquals(0,p3.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        assertEquals(1,p4.getCharacterState().getDamageBar().size());
        assertEquals(0,p4.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        assertEquals(1,p5.getCharacterState().getDamageBar().size());
        assertEquals(0,p5.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        assertEquals(0, p1.getCharacterState().getAmmoBag().get(AmmoColor.YELLOW).intValue());



    }

    @Test
    public void testSledgehammer() {
        Weapon weapon = weaponList.stream().filter(w -> w.getName().equals("Sledgehammer")).findFirst().orElse(null);
        System.out.println(weapon.getName());

        game.setCurrentPlayer(p1); // the attacker
        p1.getCharacterState().setTile(tileMap[0][0]);
		p1.getCharacterState().addWeapon(weapon);
        p2.getCharacterState().resetDamageBar();
        p2.getCharacterState().resetMarkerBar();
        p3.getCharacterState().resetDamageBar();
        p3.getCharacterState().resetMarkerBar();
        p4.getCharacterState().resetDamageBar();
        p4.getCharacterState().resetMarkerBar();
        p5.getCharacterState().resetDamageBar();
        p5.getCharacterState().resetMarkerBar();

        // testing the availability of the action units
        assertTrue(weapon.isLoaded());
        assertTrue(weapon.getActionUnitList().stream()
                .map(ActionUnit::getName)
                .collect(Collectors.toList()).containsAll(Arrays.asList("Basic Mode", "Pulverize Mode"))
        );
        assertTrue(weapon.getOptionalEffectList().stream()
                .map(ActionUnit::getName)
                .collect(Collectors.toList()).containsAll(Arrays.asList())
        );

        // testing Basic Mode --------------------------------------------------------------------
        System.out.println("Basic Mode --------------------------------------------------------------------");
        ActionUnit basicMode = weapon.getActionUnitList().stream()
                .filter(au -> au.getName().equals("Basic Mode")).findFirst().orElse(null);

        Map<String, List<Targetable>> targetableMap;
        List<Targetable> targetableList;

        // 1)
        System.out.println("1) Fail, not attacker tile");
        targetableMap = new HashMap<>();
        targetableList = new ArrayList<>();
        p2.getCharacterState().setTile(tileMap[0][1]);
        targetableList.add(p2);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);
        assertFalse(basicMode.check(game, targetableMap));
        // 2)
        System.out.println("2) System all green");
        targetableMap = new HashMap<>();
        targetableList = new ArrayList<>();
        p2.getCharacterState().setTile(tileMap[0][0]);
        targetableList.add(p2);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);
        assertTrue(basicMode.check(game, targetableMap));
        basicMode.run(game, targetableMap);
        assertEquals(2,p2.getCharacterState().getDamageBar().size());
        assertEquals(0,p2.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        p2.getCharacterState().resetDamageBar();
        p2.getCharacterState().resetMarkerBar();

        // testing Pulverize Mode --------------------------------------------------------------------
        System.out.println("Pulverize Mode --------------------------------------------------------------------");
        ActionUnit pulverizeMode = weapon.getActionUnitList().stream()
                .filter(au -> au.getName().equals("Pulverize Mode")).findFirst().orElse(null);

        // 1)
        System.out.println("1) Fail, not enough ammo");
        assertFalse(pulverizeMode.check(game, targetableMap));
        p1.getCharacterState().getAmmoBag().put(AmmoColor.RED,1);
        // 2)
        System.out.println("2) Fail, not attacker tile");
        targetableMap = new HashMap<>();
        targetableList = new ArrayList<>();
        p2.getCharacterState().setTile(tileMap[0][1]);
        targetableList.add(p2);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);
        assertFalse(basicMode.check(game, targetableMap));
        // 3)
        System.out.println("3) Fail, distance violation");
        targetableMap = new HashMap<>();
        targetableList = new ArrayList<>();
        p2.getCharacterState().setTile(tileMap[0][0]);
        targetableList.add(p2);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);
        targetableList = new ArrayList<>();
        targetableList.add(tileMap[0][3]);
        targetableMap.put(CommandConstants.TILELIST, targetableList);
        assertFalse(pulverizeMode.check(game, targetableMap));
        // 4)
        System.out.println("4) Fail, not unidirectional");
        targetableMap = new HashMap<>();
        targetableList = new ArrayList<>();
        p2.getCharacterState().setTile(tileMap[0][0]);
        targetableList.add(p2);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);
        targetableList = new ArrayList<>();
        targetableList.add(tileMap[1][1]);
        targetableMap.put(CommandConstants.TILELIST, targetableList);
        assertFalse(pulverizeMode.check(game, targetableMap));
        // 5)
        System.out.println("5) System all green");
        targetableMap = new HashMap<>();
        targetableList = new ArrayList<>();
        p2.getCharacterState().setTile(tileMap[0][0]);
        targetableList.add(p2);
        targetableMap.put(CommandConstants.TARGETLIST, targetableList);
        targetableList = new ArrayList<>();
        targetableList.add(tileMap[0][2]);
        targetableMap.put(CommandConstants.TILELIST, targetableList);
        assertTrue(pulverizeMode.check(game, targetableMap));
        pulverizeMode.run(game, targetableMap);
        assertEquals(3,p2.getCharacterState().getDamageBar().size());
        assertEquals(0,p2.getCharacterState().getMarkerBar().get(p1.getColor()).intValue());
        assertEquals(tileMap[0][2], p2.getCharacterState().getTile());
        assertEquals(0, p2.getCharacterState().getAmmoBag().get(AmmoColor.RED).intValue());

    }
}
