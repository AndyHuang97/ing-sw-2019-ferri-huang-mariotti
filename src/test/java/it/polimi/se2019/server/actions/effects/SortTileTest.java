package it.polimi.se2019.server.actions.effects;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Board;
import it.polimi.se2019.server.games.board.LinkType;
import it.polimi.se2019.server.games.board.RoomColor;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.CharacterState;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.util.CommandConstants;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class SortTileTest {

    Game game;
    Tile[][] tileMap;
    Board board;

    @Before
    public void setUp() {
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
        Player p1 = new Player("P1");
        p1.setCharacterState(new CharacterState());
        p1.getCharacterState().setTile(tileMap[0][0]);
        p1.setActive(true);
        game.setCurrentPlayer(p1);
    }

    @Test
    public void testSortTile() {
        Effect effect = new SortTile();
        List<Targetable> tileList = new ArrayList<>();
        Map<String, List<Targetable>> targets = new HashMap<>();

        System.out.println(game.getCurrentPlayer());
        Tile baseTile = game.getCurrentPlayer().getCharacterState().getTile();

        tileList.add(tileMap[0][2]);
        tileList.add(tileMap[0][1]);
        tileList.add(tileMap[0][3]);
        System.out.println(tileList);
        targets.put(CommandConstants.TILELIST, tileList);
        effect.run(game, targets);
        System.out.println(tileList);
        assertEquals(tileMap[0][1], targets.get(CommandConstants.TILELIST).get(0));
        assertEquals(tileMap[0][2], targets.get(CommandConstants.TILELIST).get(1));
        assertEquals(tileMap[0][3], targets.get(CommandConstants.TILELIST).get(2));



    }
}