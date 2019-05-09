package it.polimi.se2019.server;

import it.polimi.se2019.server.actions.Action;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.player.CharacterState;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.games.player.PlayerColor;
import it.polimi.se2019.server.users.ConcreteAction;
import it.polimi.se2019.server.users.UserData;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PlayerActionTest {

    PlayerAction playerAction;

    @Before
    public void setUp() {
        Player player= new Player(false, null, null, PlayerColor.BLUE);
        playerAction = new ConcreteAction(new Game(), player, Action.MMM);
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
        Player newPlayer = new Player(true, new UserData("Nick1"), new CharacterState(), PlayerColor.BLUE);

        playerAction.setPlayer(newPlayer);

        Assert.assertEquals(newPlayer, playerAction.getPlayer());
    }

    @Test
    public void testSetAction() {
        Action newAction = Action.MS;

        playerAction.setAction(newAction);

        Assert.assertEquals(newAction, playerAction.getAction());
    }
}