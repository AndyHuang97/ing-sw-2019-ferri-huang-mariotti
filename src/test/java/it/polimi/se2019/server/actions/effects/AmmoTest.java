package it.polimi.se2019.server.actions.effects;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.*;
import it.polimi.se2019.server.games.player.AmmoColor;
import it.polimi.se2019.server.games.player.CharacterState;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.games.player.PlayerColor;
import it.polimi.se2019.server.users.UserData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class AmmoTest {

    Tile tile;
    Tile[][] tileMap;
    Board board;
    Game game;
    Player p1, p2, p3, p4;
    Map<String, List<Targetable>> targets;
    List<Targetable> list;

    @Before
    public void setUp() {
        targets = new HashMap<>();
        game = new Game();
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
        game.setBoard(board);

        p1 = new Player(true, new UserData("A"), new CharacterState(), PlayerColor.BLUE);
        p1.getCharacterState().setTile(tileMap[1][0]);
        p2 = new Player(true, new UserData("B"), new CharacterState(), PlayerColor.GREEN);
        p2.getCharacterState().setTile(tileMap[1][1]);
        p3 = new Player(true, new UserData("C"), new CharacterState(), PlayerColor.YELLOW);
        p3.getCharacterState().setTile(tileMap[1][2]);
        p4 = new Player(true, new UserData("D"), new CharacterState(), PlayerColor.GREY);
        p4.getCharacterState().setTile(tileMap[0][1]);
        game.setPlayerList(new ArrayList<>(Arrays.asList(p1,p2,p3,p4)));

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
        targets = null;
        list = null;
    }

    @Test
    public void testAddAmmo() {
        Effect effect;
        game.setCurrentPlayer(p1);
        EnumMap<AmmoColor, Integer> ammoBag = new EnumMap<>(AmmoColor.class);
        ammoBag.put(AmmoColor.BLUE, 0);
        ammoBag.put(AmmoColor.RED, 0);
        ammoBag.put(AmmoColor.YELLOW, 0);
        p1.getCharacterState().setAmmoBag(ammoBag);

        EnumMap<AmmoColor, Integer> ammoToAdd = new EnumMap<>(AmmoColor.class);
        ammoToAdd.put(AmmoColor.BLUE, 1);
        ammoToAdd.put(AmmoColor.RED, 2);
        ammoToAdd.put(AmmoColor.YELLOW, 3);

        effect = new AddAmmo(ammoToAdd);
        effect.run(game, null);
        assertEquals(1, p1.getCharacterState().getAmmoBag().get(AmmoColor.BLUE).intValue());
        assertEquals(2, p1.getCharacterState().getAmmoBag().get(AmmoColor.RED).intValue());
        assertEquals(3, p1.getCharacterState().getAmmoBag().get(AmmoColor.YELLOW).intValue());

        ammoToAdd.put(AmmoColor.BLUE, 4);
        ammoToAdd.put(AmmoColor.RED, 4);
        ammoToAdd.put(AmmoColor.YELLOW, 4);

        effect.run(game, null);
        assertEquals(3, p1.getCharacterState().getAmmoBag().get(AmmoColor.BLUE).intValue());
        assertEquals(3, p1.getCharacterState().getAmmoBag().get(AmmoColor.RED).intValue());
        assertEquals(3, p1.getCharacterState().getAmmoBag().get(AmmoColor.YELLOW).intValue());

    }

    @Test
    public void testConsumeAmmo() {
        Effect effect;
        game.setCurrentPlayer(p1);
        EnumMap<AmmoColor, Integer> ammoBag = new EnumMap<>(AmmoColor.class);
        ammoBag.put(AmmoColor.BLUE, 3);
        ammoBag.put(AmmoColor.RED, 3);
        ammoBag.put(AmmoColor.YELLOW, 3);
        p1.getCharacterState().setAmmoBag(ammoBag);

        EnumMap<AmmoColor, Integer> ammoToConsume = new EnumMap<>(AmmoColor.class);
        ammoToConsume.put(AmmoColor.BLUE, 1);
        ammoToConsume.put(AmmoColor.RED, 2);
        ammoToConsume.put(AmmoColor.YELLOW, 3);

        effect = new ConsumeAmmo(ammoToConsume);
        effect.run(game, null);
        assertEquals(2, p1.getCharacterState().getAmmoBag().get(AmmoColor.BLUE).intValue());
        assertEquals(1, p1.getCharacterState().getAmmoBag().get(AmmoColor.RED).intValue());
        assertEquals(0, p1.getCharacterState().getAmmoBag().get(AmmoColor.YELLOW).intValue());

        // a consumption that makes an ammo color's valure negative is not possible for the conditions
    }
}