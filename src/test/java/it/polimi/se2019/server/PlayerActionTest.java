package it.polimi.se2019.server;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.player.CharacterState;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.games.player.PlayerColor;
import it.polimi.se2019.server.playeractions.GrabPlayerAction;
import it.polimi.se2019.server.playeractions.PlayerAction;
import it.polimi.se2019.server.users.UserData;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

public class PlayerActionTest {

    PlayerAction playerAction;

    @Before
    public void setUp() {
        Player player= new Player(UUID.randomUUID().toString(),false, null, null, PlayerColor.BLUE);
        playerAction = new GrabPlayerAction(new Game(), player);
    }

    @After
    public void tearDown() {
        playerAction = null;
    }

    @Test
    public void testSetGame() {
        Game newGame = new Game();

        playerAction.setGame(newGame);

        Assert.assertEquals(newGame, playerAction.getGame());
    }

    @Test
    public void testSetPlayer() {
        Player newPlayer = new Player(UUID.randomUUID().toString(), true, new UserData("Nick1"), new CharacterState(), PlayerColor.BLUE);

        playerAction.setPlayer(newPlayer);

        Assert.assertEquals(newPlayer, playerAction.getPlayer());
    }
}