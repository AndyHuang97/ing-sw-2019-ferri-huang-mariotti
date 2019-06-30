package it.polimi.se2019.server.games;

import it.polimi.se2019.server.games.player.CharacterState;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.games.player.PlayerColor;
import it.polimi.se2019.server.users.UserData;
import org.junit.After;
import org.junit.Before;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlayerDeathTest {

    PlayerDeath pd;
    List<PlayerColor> damageBar;
    PlayerColor p1, p2, p3;
    Player player;

    @Before
    public void setUp() {
        p1 = PlayerColor.GREEN;
        p2 = PlayerColor.PURPLE;
        p3 = PlayerColor.YELLOW;
        damageBar = new ArrayList<>(Arrays.asList(p3,p3,p3,p2,p2,p3,p3,p2,p2,p3,p1));
        player = new Player("", true, new UserData("Jon Snow"), new CharacterState(), PlayerColor.BLUE);
        player.getCharacterState().setDamageBar(damageBar);
        player.getCharacterState().setDeaths(0);
        pd = new PlayerDeath(player, false);

    }

    @After
    public void tearDown() {
        p1 = null;
        p2 = null;
        p3 = null;
        player = null;
        pd = null;
        damageBar = null;
    }

}