package it.polimi.se2019.server.games;

import it.polimi.se2019.server.cards.powerup.PowerUp;
import it.polimi.se2019.server.cards.weapons.Weapon;
import it.polimi.se2019.server.games.board.Board;
import it.polimi.se2019.server.games.player.CharacterState;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.games.player.PlayerColor;
import it.polimi.se2019.server.users.UserData;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

public class GameTest {

    Game game;

    @Before
    public void setUp() {
        game = new Game();
    }

    @After
    public void tearDown() {
        game = null;
    }

    @Test
    public void testGenerateGameData() {

        GameData gameData = game.generateGameData();

        Assert.assertEquals(game.getStartDate(), gameData.getStartDate());
    }

    @Test
    public void testUpdateTurn() {
    }

    @Test
    public void testSetCurrentPlayer_NotActivePlayer() {

        Player nextPlayer = new Player(UUID.randomUUID().toString(), false, new UserData("Nick"), new CharacterState(), PlayerColor.BLUE);
        Player currPlayer = game.getCurrentPlayer();

        game.setCurrentPlayer(nextPlayer);

        Assert.assertEquals(currPlayer, game.getCurrentPlayer());
    }

    @Test
    public void testSetCurrentPlayer_ActivePlayer() {

        Player nextPlayer = new Player(UUID.randomUUID().toString(), true, new UserData("Nick"), new CharacterState(), PlayerColor.BLUE);

        game.setCurrentPlayer(nextPlayer);

        Assert.assertEquals(nextPlayer, game.getCurrentPlayer());
    }

    @Test
    public void testSetStartDate() {

        Date newDate = new Date();

        game.setStartDate(newDate);

        Assert.assertEquals(newDate, game.getStartDate());
    }

    @Test
    public void testSetPlayerList() {

        Player p1 = new Player(UUID.randomUUID().toString(), false, new UserData("Nick1"), new CharacterState(), PlayerColor.BLUE);
        Player p2 = new Player(UUID.randomUUID().toString(), true, new UserData("Nick2"), new CharacterState(), PlayerColor.GREEN);
        ArrayList<Player> playerList = new ArrayList<>(Arrays.asList(p1, p2));

        game.setPlayerList(playerList);

        Assert.assertEquals(playerList, game.getPlayerList());
    }

    @Test
    public void testSetBoard() {

        Board board = new Board();

        game.setBoard(board);

        Assert.assertEquals(board, game.getBoard());
    }

    @Test
    public void testSetKillshotTrack() {

        Integer killshots = 1;

        game.setKillshotTrack(killshots);

        Assert.assertEquals(killshots, game.getKillshotTrack());
    }

    @Test
    public void testSetWeaponDeck() {

        Deck<Weapon> weaponDeck = new Deck<>(null);

        game.setWeaponDeck(weaponDeck);

        Assert.assertEquals(weaponDeck, game.getWeaponDeck());
    }

    @Test
    public void testSetPowerupDeck() {

        Deck<PowerUp> powerupDeck = new Deck<>(null);

        game.setPowerupDeck(powerupDeck);

        Assert.assertEquals(powerupDeck, game.getPowerupDeck());
    }
}