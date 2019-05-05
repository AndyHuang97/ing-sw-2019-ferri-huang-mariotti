package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.board.NormalTile;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.CharacterState;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.games.player.PlayerColor;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class IsNotAttackerTileTest {

    Player player;
    NormalTile tile1;
    NormalTile tile2;

    @Before
    public void setUp() throws Exception {
        tile1 = new NormalTile(null, null, null);
        tile2 = new NormalTile(null, null, null);
        CharacterState state = new CharacterState(null, null, null, null, tile1, null);
        player = new Player(false, null, state, PlayerColor.BLUE);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testAttackerTile() {
        IsNotAttackerTile testCheck1 = new IsNotAttackerTile(tile1, player);
        Assert.assertEquals(testCheck1.check(), false);
        IsNotAttackerTile testCheck2 = new IsNotAttackerTile(tile2, player);
        Assert.assertEquals(testCheck2.check(), true);
    }
}