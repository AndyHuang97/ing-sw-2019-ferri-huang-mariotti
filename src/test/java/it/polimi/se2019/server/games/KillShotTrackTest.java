package it.polimi.se2019.server.games;

import it.polimi.se2019.server.games.player.CharacterState;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.games.player.PlayerColor;
import it.polimi.se2019.server.users.UserData;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;


public class KillShotTrackTest {

    KillShotTrack kt;
    Player p1, p2, p3, p4 ,p5;
    PlayerColor pc1, pc3, pc4;
    List<PlayerColor> damageBar;
    Game game;

    @Before
    public void setUp() {
        p1 = new Player(UUID.randomUUID().toString(),true, new UserData("A"), new CharacterState(), PlayerColor.BLUE);
        p2 = new Player(UUID.randomUUID().toString(),true, new UserData("B"), new CharacterState(), PlayerColor.GREEN);
        p3 = new Player(UUID.randomUUID().toString(),true, new UserData("C"), new CharacterState(), PlayerColor.GREY);
        p4 = new Player(UUID.randomUUID().toString(),true, new UserData("D"), new CharacterState(), PlayerColor.PURPLE);
        p5 = new Player(UUID.randomUUID().toString(),true, new UserData("E"), new CharacterState(), PlayerColor.YELLOW);
        pc1 = p1.getColor();
        pc3 = p3.getColor();
        pc4 = p4.getColor();
        damageBar = new ArrayList<>(Arrays.asList(pc3,pc3,pc3,pc4,pc4,pc3,pc3,pc4,pc4,pc3,pc1));
        p2.getCharacterState().setDamageBar(damageBar);
        p5.getCharacterState().setDamageBar(damageBar);
        kt = new KillShotTrack(new ArrayList<>(Arrays.asList(p1, p2, p3, p4, p5)));

        game = new Game(null, Arrays.asList(p1,p2,p3,p4,p5), p1, null, kt, null, null, null);
    }

    @After
    public void tearDown() {

        p1 = null;
        p2 = null;
        p3 = null;
        p4 = null;
        p5 = null;
        pc1 = null;
        pc3 = null;
        pc4 = null;
        kt = null;
    }

    @Test
    public void testCreation() {

        Map<Integer, EnumMap<PlayerColor, Integer>> existingDeathTrack = new HashMap<>();
        List<Player>  playerList = new ArrayList<>(Arrays.asList(p1, p2, p3, p4, p5));

        kt = new KillShotTrack(existingDeathTrack, playerList, 0);

        Assert.assertEquals(existingDeathTrack, kt.getDeathTrack());
        Assert.assertEquals(0, kt.getKillCounter().intValue());
    }

    @Test
    public void testAddDeath_NoOverkill() {
        PlayerColor playerColor = p1.getColor();

        p2.getCharacterState().addDamage(playerColor,11, game);

        kt.setKillCounter(0);
        kt.addDeath(p2,false);

        Map<Integer, EnumMap<PlayerColor, Integer>> deathTrack = kt.getDeathTrack();
        Map<PlayerColor, Integer> deathSlot = deathTrack.get(0);
        Integer deathValue = deathSlot.get(playerColor);

        Assert.assertEquals(1, deathValue.intValue());
    }

    @Test
    public void testAddDeath_Overkill() {
        PlayerColor playerColor = p1.getColor();

        p2.getCharacterState().addDamage(playerColor,12, game);

        kt.setKillCounter(1);
        kt.addDeath(p2, true);

        Map<Integer, EnumMap<PlayerColor, Integer>> deathTrack = kt.getDeathTrack();
        Map<PlayerColor, Integer> deathSlot = deathTrack.get(1);
        Integer deathValue = deathSlot.get(playerColor);

        Assert.assertEquals(2, deathValue.intValue());
    }

    @Test
    public void testAddDeath_FinalFrenzy() {
        kt.setKillCounter(kt.getKillsForFrenzy()-1);

        p2.getCharacterState().resetDamageBar();
        p2.getCharacterState().addDamage(p3.getColor(), 11, game);
        game.addDeath(p2, false);

        p2.getCharacterState().resetDamageBar();
        p2.getCharacterState().addDamage(p3.getColor(), 12, game);
        game.addDeath(p2, true);

        p5.getCharacterState().resetDamageBar();
        p5.getCharacterState().addDamage(p4.getColor(), 12, game);
        game.addDeath(p5, true);

        Map<Integer, EnumMap<PlayerColor, Integer>> deathTrack = kt.getDeathTrack();

        Map<PlayerColor, Integer> trackSlot = deathTrack.get(kt.getKillsForFrenzy()-1);
        Integer p2DeathValue = trackSlot.get(p3.getColor());
        Integer p5DeathValue = trackSlot.get(p4.getColor());

        Assert.assertEquals(3, p2DeathValue.intValue());
        Assert.assertEquals(2, p5DeathValue.intValue());
    }

    @Test
    public void testSetDeathTrack() {
        Map<Integer, EnumMap<PlayerColor, Integer>> expectedDeathTrack = new HashMap<>();

        kt.setDeathTrack(expectedDeathTrack);

        Assert.assertEquals(expectedDeathTrack, kt.getDeathTrack());
    }

    @Test
    public void testSetTrackState() {
        kt.setKillCounter(1);

        Assert.assertEquals(1, kt.getKillCounter().intValue());
    }
}