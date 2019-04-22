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


public class KillshotTrackTest {

    KillshotTrack kt;
    Player p1, p2, p3, p4 ,p5;
    PlayerColor pc1, pc3, pc4;
    List<PlayerColor> damageBar;

    @Before
    public void setUp() {
        p1 = new Player(true, new UserData("A"), new CharacterState(), PlayerColor.BLUE);
        p2 = new Player(true, new UserData("B"), new CharacterState(), PlayerColor.GREEN);
        p3 = new Player(true, new UserData("C"), new CharacterState(), PlayerColor.GREY);
        p4 = new Player(true, new UserData("D"), new CharacterState(), PlayerColor.PURPLE);
        p5 = new Player(true, new UserData("E"), new CharacterState(), PlayerColor.YELLOW);
        pc1 = p1.getColor();
        pc3 = p3.getColor();
        pc4 = p4.getColor();
        damageBar = new ArrayList<>(Arrays.asList(pc3,pc3,pc3,pc4,pc4,pc3,pc3,pc4,pc4,pc3,pc1));
        p2.getCharacterState().setDamageBar(damageBar);
        p5.getCharacterState().setDamageBar(damageBar);
        kt = new KillshotTrack(new ArrayList<>(Arrays.asList(p1, p2, p3, p4, p5)));
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

        EnumMap<TrackState, EnumMap<PlayerColor, Integer>> existingDeathTrack = new EnumMap<>(TrackState.class);
        List<Player>  playerList = new ArrayList<>(Arrays.asList(p1, p2, p3, p4, p5));

        kt = new KillshotTrack(existingDeathTrack, playerList, TrackState.FIRSTKILL);

        Assert.assertEquals(existingDeathTrack, kt.getDeathTrack());
        Assert.assertEquals(TrackState.FIRSTKILL, kt.getTrackState());
    }

    @Test
    public void testAddDeath_NoOverkill() {

        kt.setTrackState(TrackState.FIFTHKILL);
        kt.addDeath(p2,false);

        Map<TrackState, EnumMap<PlayerColor, Integer>> deathTrack = kt.getDeathTrack();
        Map<PlayerColor, Integer> deathSlot = deathTrack.get(TrackState.FIFTHKILL);
        Integer deathValue = deathSlot.get(p2.getColor());

        Assert.assertEquals(1, deathValue.intValue());
    }

    @Test
    public void testAddDeath_Overkill() {

        kt.setTrackState(TrackState.SECONDKILL);
        kt.addDeath(p2, true);

        Map<TrackState, EnumMap<PlayerColor, Integer>> deathTrack = kt.getDeathTrack();
        Map<PlayerColor, Integer> deathSlot = deathTrack.get(TrackState.SECONDKILL);
        Integer deathValue = deathSlot.get(p2.getColor());

        Assert.assertEquals(2, deathValue.intValue());
    }

    @Test
    public void testAddDeath_FinalFrenzy() {

        kt.setTrackState(TrackState.EIGTHFRENZY);
        kt.addDeath(p2, false);
        kt.addDeath(p2, true);
        kt.addDeath(p5, true);

        Map<TrackState, EnumMap<PlayerColor, Integer>> deathTrack = kt.getDeathTrack();
        Map<PlayerColor, Integer> trackSlot = deathTrack.get(TrackState.EIGTHFRENZY);
        Integer p2DeathValue = trackSlot.get(p2.getColor());
        Integer p5DeathValue = trackSlot.get(p5.getColor());

        Assert.assertEquals(3, p2DeathValue.intValue());
        Assert.assertEquals(2, p5DeathValue.intValue());
    }

    @Test
    public void testNextState() {

        kt.nextState();
        Assert.assertEquals(TrackState.SECONDKILL, kt.getTrackState());
        kt.nextState();
        Assert.assertEquals(TrackState.THIRDKILL, kt.getTrackState());
        kt.nextState();
        Assert.assertEquals(TrackState.FOURTHKILL, kt.getTrackState());
        kt.nextState();
        Assert.assertEquals(TrackState.FIFTHKILL, kt.getTrackState());
        kt.nextState();
        Assert.assertEquals(TrackState.SIXTHKILL, kt.getTrackState());
        kt.nextState();
        Assert.assertEquals(TrackState.SEVENTHKILL, kt.getTrackState());
        kt.nextState();
        Assert.assertEquals(TrackState.EIGTHFRENZY, kt.getTrackState());
        kt.nextState();
        Assert.assertEquals(TrackState.EIGTHFRENZY, kt.getTrackState());
    }

    @Test
    public void testSetDeathTrack() {
        EnumMap<TrackState, EnumMap<PlayerColor, Integer>> expectedDeathTrack = new EnumMap<>(TrackState.class);

        kt.setDeathTrack(expectedDeathTrack);

        Assert.assertEquals(expectedDeathTrack, kt.getDeathTrack());
    }

    @Test
    public void testSetTrackState() {
        kt.setTrackState(TrackState.SECONDKILL);

        Assert.assertEquals(TrackState.SECONDKILL, kt.getTrackState());
    }
}