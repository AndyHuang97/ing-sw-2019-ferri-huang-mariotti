package it.polimi.se2019.server.games.player;

import it.polimi.se2019.server.users.UserData;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

public class PlayerTest {

    Player player;

    @Before
    public void setUp() {
        player = new Player(UUID.randomUUID().toString(),false, new UserData("Nick1"), new CharacterState(), PlayerColor.BLUE);
    }

    @After
    public void tearDown() {
        player = null;
    }

    @Test
    public void testSetActive() {
        player.setActive(true);

        Assert.assertEquals(true, player.getActive());
    }

    @Test
    public void testSetUserData() {
        UserData usrData = new UserData("Nick2");

        player.setUserData(usrData);

        Assert.assertEquals(usrData, player.getUserData());
    }

    @Test
    public void testSetCharacterState() {
        CharacterState newChrState = new CharacterState();

        player.setCharacterState(newChrState);

        Assert.assertEquals(newChrState, player.getCharacterState());
    }

}