package it.polimi.se2019.server.dataupdate;

import it.polimi.se2019.client.View;
import it.polimi.se2019.client.cli.CLIView;
import it.polimi.se2019.server.cards.ammocrate.AmmoCrate;
import it.polimi.se2019.server.cards.weapons.Weapon;
import it.polimi.se2019.server.exceptions.PlayerNotFoundException;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.player.CharacterState;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.games.player.PlayerColor;
import it.polimi.se2019.server.users.UserData;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class DataUpdateTest {
    private static final String PLAYERNICK = "testPlayer";

    private Player player;
    private View view;
    private CharacterState characterState;
    private Game game;

    @Before
    public void setUp() {
        // Initialize the view
        view = new CLIView();

        // Initialize Game and Player
        game = new Game();

        UserData userData = new UserData(PLAYERNICK);
        String id = PLAYERNICK;
        characterState = new CharacterState();

        player = new Player(PLAYERNICK, true, userData, characterState, PlayerColor.BLUE);

        characterState.register(game);
        player.register(game);
        game.register(view);

        List<Player> playerList = new ArrayList<>();
        playerList.add(player);

        game.setPlayerList(playerList);
        game.initGameObjects("0");

        view.getModel().setGame(game);


    }

    @Test
    public void testCharacterStateUpdate() throws PlayerNotFoundException {
        // Build a Response for a CharacterState update and send it to the view
        characterState.setScore(42);

        // Check if the CharacterState of the view has been updated
        Integer score = view.getModel().getGame().getPlayerByNickname(PLAYERNICK).getCharacterState().getScore();

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
}
