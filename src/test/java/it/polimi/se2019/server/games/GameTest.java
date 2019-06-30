package it.polimi.se2019.server.games;

import it.polimi.se2019.server.cards.powerup.PowerUp;
import it.polimi.se2019.server.cards.weapons.Weapon;
import it.polimi.se2019.server.games.board.Board;
import it.polimi.se2019.server.games.player.CharacterState;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.games.player.PlayerColor;
import it.polimi.se2019.server.users.UserData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GameTest {

    Game game;

    @Before
    public void setUp() {
        game = new Game();
        Player p1 = new Player(UUID.randomUUID().toString(),true, new UserData("Nick1"), new CharacterState(), PlayerColor.BLUE);
        Player p2 = new Player(UUID.randomUUID().toString(),false, new UserData("Nick2"), new CharacterState(), PlayerColor.GREEN);
        Player p3 = new Player(UUID.randomUUID().toString(),false, new UserData("Nick3"), new CharacterState(), PlayerColor.PURPLE);
        game.getPlayerList().addAll(Arrays.asList(p1,p2,p3));
    }

    @After
    public void tearDown() {
        game = null;
    }

    @Test
    public void testGenerateGameData() {

        GameData gameData = game.generateGameData();

        assertEquals(game.getStartDate(), gameData.getStartDate());
    }

    @Test
    public void testInitGameObjects() {
        game.initGameObjects("0");
        assertEquals("0", game.getBoard().getId());
        game.getPlayerList()
                .forEach(p -> assertEquals(2, p.getCharacterState().getPowerUpBag().size()));
        game.getBoard().getTileList().stream()
                .filter(Objects::nonNull)
                .filter(t -> t.isSpawnTile())
                .forEach(t -> {
                    assertTrue(t.getAmmoCrate() == null);
                    assertEquals(3, t.getWeaponCrate().size());
                });
        game.getBoard().getTileList().stream()
                .filter(Objects::nonNull)
                .filter(t -> !t.isSpawnTile())
                .forEach(t -> {
                    assertTrue(t.getAmmoCrate() != null);
                    assertEquals(0, t.getWeaponCrate().size());
                });
    }


    @Test
    public void testUpdateTurn() {
    }

    @Test
    public void testSetCurrentPlayer_NotActivePlayer() {

        Player p1 = new Player(UUID.randomUUID().toString(), false, new UserData("Nick"), new CharacterState(), PlayerColor.BLUE);
        p1.setActive(true);

        game.setCurrentPlayerNotify(p1);

        assertEquals(p1, game.getCurrentPlayer());

        Player p2 = new Player(UUID.randomUUID().toString(), false, new UserData("Nick"), new CharacterState(), PlayerColor.BLUE);
        p2.setActive(false);
        game.setCurrentPlayerNotify(p1);
        assertEquals(p1, game.getCurrentPlayer());

    }

    @Test
    public void testSetCurrentPlayer_ActivePlayer() {

        Player nextPlayer = new Player(UUID.randomUUID().toString(), true, new UserData("Nick"), new CharacterState(), PlayerColor.BLUE);

        game.setCurrentPlayerNotify(nextPlayer);

        assertEquals(nextPlayer, game.getCurrentPlayer());
    }

    @Test
    public void testSetStartDate() {

        Date newDate = new Date();

        game.setStartDate(newDate);

        assertEquals(newDate, game.getStartDate());
    }

    @Test
    public void testSetPlayerList() {

        Player p1 = new Player(UUID.randomUUID().toString(), false, new UserData("Nick1"), new CharacterState(), PlayerColor.BLUE);
        Player p2 = new Player(UUID.randomUUID().toString(), true, new UserData("Nick2"), new CharacterState(), PlayerColor.GREEN);
        ArrayList<Player> playerList = new ArrayList<>(Arrays.asList(p1, p2));

        game.setPlayerList(playerList);

        assertEquals(playerList, game.getPlayerList());
    }

    @Test
    public void testSetBoard() {

        Board board = new Board();

        game.setBoard(board);

        assertEquals(board, game.getBoard());
    }

    @Test
    public void testSetKillshotTrack() {

        Integer killShots = 1;

        game.getKillshotTrack().setKillCounter(killShots);

        assertEquals(killShots, game.getKillshotTrack().getKillCounter());
    }

    @Test
    public void testSetWeaponDeck() {

        Deck<Weapon> weaponDeck = new Deck<>(null);

        game.setWeaponDeck(weaponDeck);

        assertEquals(weaponDeck, game.getWeaponDeck());
    }

    @Test
    public void testSetPowerupDeck() {

        Deck<PowerUp> powerupDeck = new Deck<>(null);

        game.setPowerupDeck(powerupDeck);

        assertEquals(powerupDeck, game.getPowerupDeck());
    }
}