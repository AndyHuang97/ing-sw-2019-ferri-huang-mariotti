package it.polimi.se2019.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.polimi.se2019.server.actions.ActionUnit;
import it.polimi.se2019.server.cards.ammocrate.AmmoCrate;
import it.polimi.se2019.server.cards.powerup.PowerUp;
import it.polimi.se2019.server.cards.weapons.Weapon;
import it.polimi.se2019.server.deserialize.BoardDeserializer;
import it.polimi.se2019.server.deserialize.DynamicDeserializerFactory;
import it.polimi.se2019.server.deserialize.TileDeserializerSupplier;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.KillShotTrack;
import it.polimi.se2019.server.games.board.Board;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.AmmoColor;
import it.polimi.se2019.server.games.player.CharacterState;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.games.player.PlayerColor;
import it.polimi.se2019.server.users.UserData;
import it.polimi.se2019.util.LocalModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.IntStream;

public class Model implements LocalModel {

    private Game game;

    @Override
    public void setCharacterState(CharacterState characterState) {
        PlayerColor color = characterState.getColor();

        Player localPlayer;
        localPlayer = game.getPlayerByColor(color);

        localPlayer.setCharacterState(characterState);
        Logger.getGlobal().info("Character update: " + localPlayer.getCharacterState().getColor() + "\ttile "+localPlayer.getCharacterState().getTile());
    }

    @Override
    public void setGame(Game game) {
        this.game = game;
    }

    @Override
    public void setKillShotTrack(KillShotTrack killShotTrack) {
        this.game.setKillshotTrack(killShotTrack);
    }

    @Override
    public Board getBoard() {
        Logger.getGlobal().info("Updating the board ...");
        return getGame().getBoard();
    }

    @Override
    public Game getGame() {
        return game;
    }



    // testing methods ---------------------------------------------------------------------------------------------
    public void initGame() {
        game = new Game();
        game.setFrenzy(true);
        boardDeserialize();

        Player p1 = new Player(UUID.randomUUID().toString(), true, new UserData("Giorno"), new CharacterState(), PlayerColor.GREEN);
        p1.getCharacterState().setTile(game.getBoard().getTile(0,0));
        Player p2 = new Player(UUID.randomUUID().toString(), true, new UserData("P2"), new CharacterState(), PlayerColor.BLUE);
        p2.getCharacterState().setTile(game.getBoard().getTile(0,0));
        Player p3 = new Player(UUID.randomUUID().toString(), true, new UserData("Narancia"), new CharacterState(), PlayerColor.YELLOW);
        p3.getCharacterState().setTile(game.getBoard().getTile(1,1));
        Player p4 = new Player(UUID.randomUUID().toString(), true, new UserData("Bucciarati"), new CharacterState(), PlayerColor.GREY);
        p4.getCharacterState().setTile(game.getBoard().getTile(2,0));
        Player p5 = new Player(UUID.randomUUID().toString(), true, new UserData("Abbacchio"), new CharacterState(), PlayerColor.PURPLE);
        p5.getCharacterState().setTile(game.getBoard().getTile(2,2));
        game.setPlayerList(Arrays.asList(p1,p2,p3,p4));
        game.setCurrentPlayer(p1);
        Weapon w1 = new Weapon(null, "SledgeHammer", null
                , null, null);
        w1.setLoaded(true);
        Weapon w2 = new Weapon(null, "Cyberblade", null
                , null, null);
        w2.setLoaded(false);
        Weapon w3 = new Weapon(null, "Furnace", null
                , null, null);
        w3.setLoaded(true);
        Weapon w4 = new Weapon(null, "Railgun", null
                , null, null);
        w4.setLoaded(false);
        p1.getCharacterState().setWeaponBag(Arrays.asList(w1,w2,w3));
        p2.getCharacterState().setWeaponBag(Arrays.asList(w4,w2,w3));
        p3.getCharacterState().setWeaponBag(Arrays.asList(w1,w4,w3));
        p4.getCharacterState().setWeaponBag(Arrays.asList(w1,w2,w4));
        p5.getCharacterState().setWeaponBag(Arrays.asList(w1,w2,w4));

        List<ActionUnit> actionUnitList = new ArrayList<>();
        actionUnitList.add(new ActionUnit(true,"Basic mode", null, null,null, 2,2,true));
        actionUnitList.add(new ActionUnit(true,"Alternate mode", null, null,null, 2,1,false));
        List<ActionUnit> optionalEffectList = new ArrayList<>();
        optionalEffectList.add(new ActionUnit(true,"Optional effect", null, null,null, 2,2,true));
        p1.getCharacterState().getWeaponBag().stream()
                .forEach(w -> {
                    w.setActionUnitList(actionUnitList);
                    w.setOptionalEffectList(optionalEffectList);
                });


        List<ActionUnit> powerUpActionList = new ArrayList<>();
        powerUpActionList.add(new ActionUnit(true,"Basic mode", null, null,null, 1,2,true));
        PowerUp newton = new PowerUp(powerUpActionList, "Blue_Newton", AmmoColor.BLUE);
        List<ActionUnit> targetingScopeList = new ArrayList<>();
        targetingScopeList.add(new ActionUnit(true,"Basic mode", null, null,null, 1,0,true));
        PowerUp targetingScope =  new PowerUp(targetingScopeList, "Red_TargetingScope", AmmoColor.RED);
        List<ActionUnit> powerUpActionList1 = new ArrayList<>();
        powerUpActionList1.add(new ActionUnit(true,"Basic mode", null, null,null, 0,1,true));
        PowerUp teleporter = new PowerUp(powerUpActionList1, "Yellow_TagbackGrenade", AmmoColor.YELLOW);
        p1.getCharacterState().setPowerUpBag(Arrays.asList(newton, targetingScope,teleporter,targetingScope));

        p1.getCharacterState().getDamageBar().addAll(Arrays.asList(PlayerColor.BLUE,PlayerColor.BLUE,PlayerColor.BLUE));
        p2.getCharacterState().getDamageBar().addAll(Arrays.asList(PlayerColor.YELLOW,PlayerColor.BLUE,PlayerColor.BLUE));
        p3.getCharacterState().getDamageBar().addAll(Arrays.asList(PlayerColor.BLUE,PlayerColor.YELLOW,PlayerColor.BLUE));
        p4.getCharacterState().getDamageBar().addAll(Arrays.asList(PlayerColor.BLUE,PlayerColor.BLUE,PlayerColor.YELLOW));
        p5.getCharacterState().getDamageBar().addAll(Arrays.asList(PlayerColor.BLUE,PlayerColor.GREEN,PlayerColor.BLUE));

        p1.getCharacterState().getMarkerBar().put(PlayerColor.BLUE, 3);
        p1.getCharacterState().getMarkerBar().put(PlayerColor.YELLOW, 2);
        p2.getCharacterState().getMarkerBar().put(PlayerColor.GREY, 3);
        p2.getCharacterState().getMarkerBar().put(PlayerColor.YELLOW, 2);
        p3.getCharacterState().getMarkerBar().put(PlayerColor.PURPLE, 1);
        p3.getCharacterState().getMarkerBar().put(PlayerColor.GREEN, 2);
        p4.getCharacterState().getMarkerBar().put(PlayerColor.BLUE, 3);
        p4.getCharacterState().getMarkerBar().put(PlayerColor.YELLOW, 2);
        p5.getCharacterState().getMarkerBar().put(PlayerColor.BLUE, 3);
        p5.getCharacterState().getMarkerBar().put(PlayerColor.GREY, 2);
        p5.getCharacterState().getMarkerBar().put(PlayerColor.YELLOW, 2);
        p5.getCharacterState().getMarkerBar().put(PlayerColor.GREEN, 1);

        p1.getCharacterState().setDeaths(1);
        p2.getCharacterState().setDeaths(5);
        p3.getCharacterState().setDeaths(3);
        p4.getCharacterState().setDeaths(4);
        p5.getCharacterState().setDeaths(5);

        p1.getCharacterState().setValueBar(CharacterState.NORMAL_VALUE_BAR);
        p2.getCharacterState().setValueBar(CharacterState.FRENZY_VALUE_BAR);
        p3.getCharacterState().setValueBar(CharacterState.NORMAL_VALUE_BAR);
        p4.getCharacterState().setValueBar(CharacterState.NORMAL_VALUE_BAR);
        p5.getCharacterState().setValueBar(CharacterState.NORMAL_VALUE_BAR);

        EnumMap<AmmoColor, Integer> ammoMap = new EnumMap<>(AmmoColor.class);
        ammoMap.putIfAbsent(AmmoColor.BLUE, 3);
        ammoMap.putIfAbsent(AmmoColor.RED, 2);
        ammoMap.putIfAbsent(AmmoColor.YELLOW, 3);

        p1.getCharacterState().setAmmoBag(ammoMap);
        p2.getCharacterState().setAmmoBag(ammoMap);
        p3.getCharacterState().setAmmoBag(ammoMap);
        p4.getCharacterState().setAmmoBag(ammoMap);
        p5.getCharacterState().setAmmoBag(ammoMap);

        p1.getCharacterState().setScore(6);
        p2.getCharacterState().setScore(2);
        p3.getCharacterState().setScore(3);
        p4.getCharacterState().setScore(4);
        p5.getCharacterState().setScore(5);

//        KillShotTrack kt = new KillShotTrack(game.getPlayerList());
//        kt.addDeath(p1, false);
//        kt.addDeath(p2, true);
//        kt.addDeath(p3, true);
//        kt.addDeath(p4, true);
//        kt.addDeath(p5, true);
//        kt.addDeath(p1, false);
//        kt.addDeath(p2, true);
//        kt.addDeath(p3, true);
//        kt.addDeath(p4, true);
//        kt.addDeath(p5, true);
//        game.setKillshotTrack(kt);

    }

    public void boardDeserialize() {
        DynamicDeserializerFactory factory = new DynamicDeserializerFactory();
        BoardDeserializer boardDeserializer = new BoardDeserializer();
        factory.registerDeserializer("tile", new TileDeserializerSupplier());

        String path = "json/maps/map0.json";

        Board board = null;

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(Model.class.getClassLoader().getResource(path).toURI())))) {

            JsonParser parser = new JsonParser();
            JsonObject json = parser.parse(bufferedReader).getAsJsonObject();

            game.setBoard(boardDeserializer.deserialize(json, factory));

            Tile[][] tileMap = game.getBoard().getTileMap();
            IntStream.range(0, tileMap[0].length)
                    .forEach(y -> IntStream.range(0, tileMap.length)
                            .forEach(x -> {
                                if (tileMap[x][y] != null) {
                                    if (!tileMap[x][y].isSpawnTile()) {
                                        tileMap[x][y].setAmmoCrate(new AmmoCrate(null, "1_Red_2_Yellow"));
                                    } else {
                                        tileMap[x][y].setWeaponCrate(
                                                Arrays.asList(
                                                        new Weapon(null, "ZX-2", null
                                                                , null, null),
                                                        new Weapon(null, "Plasma_Gun", null
                                                                , null, null)
//                                                        ,new Weapon(null, "Heatseeker", null, null, null)
                                                        ));
                                    }
                                }
                            }));
        } catch (IOException | ClassNotFoundException | URISyntaxException e) {
            Logger.getGlobal().warning(e.toString());
        }
    }
}
