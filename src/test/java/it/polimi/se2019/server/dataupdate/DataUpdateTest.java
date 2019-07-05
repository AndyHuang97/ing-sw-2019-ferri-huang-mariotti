package it.polimi.se2019.server.dataupdate;

import it.polimi.se2019.client.View;
import it.polimi.se2019.client.cli.CLIView;
import it.polimi.se2019.server.cards.ammocrate.AmmoCrate;
import it.polimi.se2019.server.cards.weapons.Weapon;
import it.polimi.se2019.server.controller.Controller;
import it.polimi.se2019.server.exceptions.PlayerNotFoundException;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.KillShotTrack;
import it.polimi.se2019.server.games.player.CharacterState;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.games.player.PlayerColor;
import it.polimi.se2019.server.users.UserData;
import it.polimi.se2019.util.Observable;
import it.polimi.se2019.util.Observer;
import it.polimi.se2019.util.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

class Forwarder extends Observable<Response> implements Observer<Response> {
    @Override
    public void update(Response message) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(message);

            ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
            Response deserialized = (Response) objectInputStream.readObject();

            notify(deserialized);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("OPS");
        }

    }
}

public class DataUpdateTest {
    private static final String PLAYERNICK1 = "testPlayer";
    private static final String PLAYERNICK2 = "testPlayer2";

    private Player player1;
    private View view;
    private Forwarder forwarder;
    private CharacterState characterState1;
    private Game game;
    private KillShotTrack killShotTrack;
    private Controller controller;

    @Before
    public void setUp() {
        // Initialize the view
        view = new CLIView();

        // Initialize the forwarder
        forwarder = new Forwarder();

        // Initialize Game and Player
        game = new Game();

        UserData userData1 = new UserData(PLAYERNICK1);
        characterState1 = new CharacterState();

        UserData userData2 = new UserData(PLAYERNICK2);
        CharacterState characterState2 = new CharacterState();

        player1 = new Player(PLAYERNICK1, true, userData1, characterState1, PlayerColor.BLUE);
        Player player2 = new Player(PLAYERNICK2, true, userData2, characterState2, PlayerColor.YELLOW);

        characterState1.register(game);
        player1.register(game);
        game.register(forwarder);
        forwarder.register(view);

        List<Player> playerList = new ArrayList<>();
        playerList.add(player1);
        playerList.add(player2);

        game.setPlayerList(playerList);
        System.out.println(game.getPlayerList());
        view.getModel().setGame(game);

        game.initGameObjects("0");

        killShotTrack = new KillShotTrack(game.getPlayerList());

        game.setKillShotTrack(killShotTrack);
        killShotTrack.register(game);

    }

    @Test
    public void testCharacterStateUpdate() throws PlayerNotFoundException {
        // Build a Response for a CharacterState update and send it to the view
        characterState1.setScore(42);

        // Check if the CharacterState of the view has been updated
        Integer score = view.getModel().getGame().getPlayerByNickname(PLAYERNICK1).getCharacterState().getScore();

        Assert.assertEquals((Integer) 42, score);
    }

    @Test
    public void testAmmoCrateUpdate() {
        // obtain an AmmoCrate object, the source is not important
        AmmoCrate ammoCrate = game.getBoard().getTile(0,0).getAmmoCrate();
        // edit the AmmoCrate name to be unique
        ammoCrate.setName("TESTNAME!!!");

        game.getBoard().setAmmoCrate(0,1, ammoCrate);

        String ammoCrateName = view.getModel().getGame().getBoard().getTile(0,1).getAmmoCrate().getName();

        Assert.assertEquals(ammoCrate.getName(), ammoCrateName);
    }

    @Test
    public void testWeaponCrateUpdate() {
        // obtain a weaponCrate (List<Weapon>), the source is not important
        List<Weapon> weaponCrate = game.getBoard().getTile(2,0).getWeaponCrate();

        weaponCrate.get(0).setName("TESTNAME_WEAPON0");
        weaponCrate.get(1).setName("TESTNAME_WEAPON1");
        weaponCrate.get(2).setName("TESTNAME_WEAPON2");

        game.getBoard().setWeaponCrate(3, 2, weaponCrate);

        String weaponName0 = view.getModel().getGame().getBoard().getTile(3, 2).getWeaponCrate().get(0).getName();
        String weaponName1 = view.getModel().getGame().getBoard().getTile(3, 2).getWeaponCrate().get(1).getName();
        String weaponName2 = view.getModel().getGame().getBoard().getTile(3, 2).getWeaponCrate().get(2).getName();

        Assert.assertEquals(weaponCrate.get(0).getName(), weaponName0);
        Assert.assertEquals(weaponCrate.get(1).getName(), weaponName1);
        Assert.assertEquals(weaponCrate.get(2).getName(), weaponName2);
    }

    @Test
    public void testCurrentPlayerUpdate() {
        Assert.assertSame(player1, view.getModel().getGame().getCurrentPlayer());

        game.nextCurrentPlayer();

        Assert.assertNotSame(player1, view.getModel().getGame().getCurrentPlayer());
    }
}
