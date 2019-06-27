package it.polimi.se2019.server.controller;

import it.polimi.se2019.server.cards.Card;
import it.polimi.se2019.server.cards.powerup.PowerUp;
import it.polimi.se2019.server.cards.weapons.Weapon;
import it.polimi.se2019.server.deserialize.DirectDeserializers;
import it.polimi.se2019.server.exceptions.UnpackingException;
import it.polimi.se2019.server.games.Deck;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Board;
import it.polimi.se2019.server.games.player.AmmoColor;
import it.polimi.se2019.server.games.player.CharacterState;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.games.player.PlayerColor;
import it.polimi.se2019.server.playerActions.*;
import it.polimi.se2019.server.users.UserData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class ControllerStateTest {

    ControllerState controllerState;
    Game game;
    Player p1, p2, p3, p4, p5;

    @Before
    public void setUp() {
        game = new Game();
        new DirectDeserializers();

        p1 = new Player("P1", true, new UserData("P1"), new CharacterState(), PlayerColor.BLUE);
        p2 = new Player("P2", true, new UserData("P2"), new CharacterState(), PlayerColor.GREEN);
        p3 = new Player("P3", true, new UserData("P3"), new CharacterState(), PlayerColor.YELLOW);
        p4 = new Player("P4", true, new UserData("P4"), new CharacterState(), PlayerColor.GREY);
        p5 = new Player("P5", true, new UserData("P5"), new CharacterState(), PlayerColor.PURPLE);
        game.setPlayerList(new ArrayList<>(Arrays.asList(p1,p2,p3,p4,p5)));

        // prepares all objects of the game, weaponcrates and ammocrates, powerups for the players
        game.initGameObjects("0");
    }

    @After
    public void tearDown() {
        game = null;
        p1 = null;
        p2 = null;
        p3 =null;
        p4 = null;
        p5 = null;
    }

    @Test
    public void testRespawn() throws UnpackingException {
        System.out.println("Testing WaitingForRespawn ...");
        Board board = game.getBoard();
        List<Targetable> targetableList;
        List<PlayerAction> playerActions;
        PlayerAction respawnAction;

        ControllerState respawnState = new WaitingForRespawn();
        game.setCurrentPlayer(p1);
        PowerUp bluePowerUp = getPowerUp("Blue_TargetingScope");
        PowerUp redPowerUp = getPowerUp("Red_TargetingScope");
        PowerUp yellowPowerUp = getPowerUp("Yellow_TargetingScope");

        playerActions = new ArrayList<>();
        playerActions.add(new NoOperation(0));
        ControllerState newState = respawnState.nextState(playerActions, game, p1);
        assertEquals(respawnState, newState);

        System.out.println("First Spawn ------------------------------------------------------");
        System.out.println("1) Blue Spawn");
        playerActions.clear();
        targetableList = new ArrayList<>();
        targetableList.add(bluePowerUp);
        respawnAction = new RespawnAction(game,p1);
        respawnAction.unpack(targetableList);
        assertEquals(bluePowerUp, respawnAction.getCard());
        p1.getCharacterState().getPowerUpBag().add(bluePowerUp);
        assertTrue(respawnAction.check());
        playerActions.add(respawnAction);
        newState = respawnState.nextState(playerActions, game, p1);
        assertEquals(WaitingForMainActions.class, newState.getClass());
        assertEquals(board.getTile(2,0), p1.getCharacterState().getTile());
        assertFalse(p1.getCharacterState().isFirstSpawn());

        System.out.println("2) Red Spawn");
        playerActions.clear();
        targetableList = new ArrayList<>();
        targetableList.add(redPowerUp);
        respawnAction = new RespawnAction(game,p1);
        respawnAction.unpack(targetableList);
        assertEquals(redPowerUp, respawnAction.getCard());
        p1.getCharacterState().getPowerUpBag().add(redPowerUp);
        p1.getCharacterState().setFirstSpawn(true);
        assertTrue(respawnAction.check());
        playerActions.add(respawnAction);
        newState = respawnState.nextState(playerActions, game, p1);
        assertEquals(WaitingForMainActions.class, newState.getClass());
        assertEquals(board.getTile(0,1), p1.getCharacterState().getTile());
        assertFalse(p1.getCharacterState().isFirstSpawn());

        System.out.println("3) Yellow Spawn");
        playerActions.clear();
        targetableList = new ArrayList<>();
        targetableList.add(yellowPowerUp);
        respawnAction = new RespawnAction(game,p1);
        respawnAction.unpack(targetableList);
        assertEquals(yellowPowerUp, respawnAction.getCard());
        p1.getCharacterState().getPowerUpBag().add(yellowPowerUp);
        p1.getCharacterState().setFirstSpawn(true);
        assertTrue(respawnAction.check());
        playerActions.add(respawnAction);
        newState = respawnState.nextState(playerActions, game, p1);
        assertEquals(WaitingForMainActions.class, newState.getClass());
        assertEquals(board.getTile(3,2), p1.getCharacterState().getTile());
        assertFalse(p1.getCharacterState().isFirstSpawn());

        System.out.println("Not First Spawn (End of turn) --------------------------------");
        p1.getCharacterState().setFirstSpawn(false);
        p2.getCharacterState().addDamage(PlayerColor.BLUE, 11, game);
        p3.getCharacterState().addDamage(PlayerColor.BLUE, 11, game);
        p4.getCharacterState().addDamage(PlayerColor.BLUE, 11, game);
        System.out.println("1) current player P1 was pushed");
        newState = respawnState.nextState(null, game, p1);
        assertEquals(respawnState, newState);
        assertEquals(p2, game.getCurrentPlayer());


        System.out.println("2) Random not valid action");
        playerActions.clear();
        respawnAction = new MovePlayerAction(game, p2);
        playerActions.add(respawnAction);    // adding action
        newState = respawnState.nextState(playerActions, game, p1);
        assertEquals(respawnState, newState);
        System.out.println("2-bis) spawn first dead player, P2");
        playerActions.clear();
        targetableList = new ArrayList<>();
        targetableList.add(yellowPowerUp);
        respawnAction = new RespawnAction(game,p2);
        respawnAction.unpack(targetableList);
        p2.getCharacterState().getPowerUpBag().add(yellowPowerUp);
        assertTrue(respawnAction.check());
        playerActions.add(respawnAction);
        newState = respawnState.nextState(playerActions, game, p2);
        assertEquals(p3.getId(), game.getCurrentPlayer().getId());
        assertEquals(respawnState, newState);
        assertEquals(board.getTile(3,2), p1.getCharacterState().getTile());

        System.out.println("3) spawn second dead player, P3");
        playerActions.clear();
        targetableList = new ArrayList<>();
        targetableList.add(yellowPowerUp);
        respawnAction = new RespawnAction(game,p3);
        respawnAction.unpack(targetableList);
        p3.getCharacterState().getPowerUpBag().add(yellowPowerUp);
        assertTrue(respawnAction.check());
        playerActions.add(respawnAction);
        newState = respawnState.nextState(playerActions, game, p3);
        assertEquals(p4.getId(), game.getCurrentPlayer().getId());
        assertEquals(respawnState, newState);
        assertEquals(board.getTile(3,2), p1.getCharacterState().getTile());

        System.out.println("4) spawn third dead player, P4");
        playerActions.clear();
        targetableList = new ArrayList<>();
        targetableList.add(yellowPowerUp);
        respawnAction = new RespawnAction(game,p4);
        respawnAction.unpack(targetableList);
        p4.getCharacterState().getPowerUpBag().add(yellowPowerUp);
        assertTrue(respawnAction.check());
        playerActions.add(respawnAction);
        p2.getCharacterState().setFirstSpawn(false);
        newState = respawnState.nextState(playerActions, game, p4);
        assertEquals(p2.getId(), game.getCurrentPlayer().getId());
        assertEquals(WaitingForMainActions.class, newState.getClass());
        assertEquals(board.getTile(3,2), p1.getCharacterState().getTile());

    }

    @Test
    public void testReload() throws UnpackingException {
        System.out.println("Testing WaitingForReload ...");
        Board board = game.getBoard();
        List<Targetable> targetableList = new ArrayList<>();
        List<PlayerAction> playerActions = new ArrayList<>();
        PlayerAction reloadAction;

        ControllerState waitingForReload = new WaitingForReload();
        Weapon weapon = getWeapon("Lock_Rifle");
        game.setCurrentPlayer(p1);
        p1.getCharacterState().addWeapon(weapon);
        weapon.setLoaded(false);

        System.out.println("1) NOP, weapon will not be loaded, no one died");
        reloadAction = new NoOperation(0);
        playerActions.add(reloadAction);
        p1.getCharacterState().resetDamageBar();
        p2.getCharacterState().resetDamageBar();
        p3.getCharacterState().resetDamageBar();
        p4.getCharacterState().resetDamageBar();
        p5.getCharacterState().resetDamageBar();
        ControllerState newState = waitingForReload.nextState(playerActions, game, p1);
        assertEquals(p2, game.getCurrentPlayer());
        assertEquals(WaitingForMainActions.class, newState.getClass());
        assertFalse(weapon.isLoaded());


        System.out.println("2) NOP, people died!");
        game.setCurrentPlayer(p1);
        p1.getCharacterState().setFirstSpawn(false);
        p3.getCharacterState().addDamage(PlayerColor.BLUE, 11, game);
        p4.getCharacterState().addDamage(PlayerColor.BLUE, 11, game);
        newState = waitingForReload.nextState(playerActions, game, p1);
        assertEquals(p3.getId(), game.getCurrentPlayer().getId());
        assertEquals(WaitingForRespawn.class, newState.getClass());
        assertFalse(weapon.isLoaded());

        System.out.println("3) Relaod weapon, no one died.");
        playerActions.clear();
        game.setCurrentPlayer(p1);
        reloadAction = new ReloadPlayerAction(game, p1);
        targetableList.add(weapon);
        reloadAction.unpack(targetableList);
        playerActions.add(reloadAction); // adding action
        p1.getCharacterState().getAmmoBag().put(AmmoColor.BLUE,2); // should succeed
        p1.getCharacterState().resetDamageBar();
        p2.getCharacterState().resetDamageBar();
        p3.getCharacterState().resetDamageBar();
        p4.getCharacterState().resetDamageBar();
        p5.getCharacterState().resetDamageBar();
        newState = waitingForReload.nextState(playerActions, game, p1);
        assertEquals(p2.getId(), game.getCurrentPlayer().getId());
        assertEquals(WaitingForMainActions.class, newState.getClass());
        assertTrue(weapon.isLoaded());
        weapon.setLoaded(false);

        System.out.println("4) Relaod weapon, people died.");
        playerActions.clear();
        game.setCurrentPlayer(p1);
        reloadAction = new ReloadPlayerAction(game, p1);
        targetableList = new ArrayList<>();
        targetableList.add(weapon);
        reloadAction.unpack(targetableList);
        playerActions.add(reloadAction); // adding action
        p1.getCharacterState().getAmmoBag().put(AmmoColor.BLUE,2); // should succeed
        p4.getCharacterState().addDamage(PlayerColor.BLUE, 11, game);
        newState = waitingForReload.nextState(playerActions, game, p1);
        assertEquals(p4.getId(), game.getCurrentPlayer().getId());
        assertEquals(WaitingForRespawn.class, newState.getClass());
        assertTrue(weapon.isLoaded());
        weapon.setLoaded(false);

        System.out.println("5) Random not valid action");
        playerActions.clear();
        game.setCurrentPlayer(p1);
        reloadAction = new MovePlayerAction(game, p1);
        playerActions.add(reloadAction);    // adding action
        newState = waitingForReload.nextState(playerActions, game, p1);
        assertEquals(waitingForReload, newState);
        assertEquals(p1.getId(), game.getCurrentPlayer().getId());

        System.out.println("6) Reload weapon failed. Weapon was already loaded");
        playerActions.clear();
        game.setCurrentPlayer(p1);
        reloadAction = new ReloadPlayerAction(game, p1);
        targetableList.add(weapon);
        weapon.setLoaded(true);
        reloadAction.unpack(targetableList);
        playerActions.add(reloadAction); // adding action
        p1.getCharacterState().getAmmoBag().put(AmmoColor.BLUE,2); // should succeed
        newState = waitingForReload.nextState(playerActions, game, p1);
        assertEquals(p1.getId(), game.getCurrentPlayer().getId());
        assertEquals(newState, waitingForReload);
        assertTrue(weapon.isLoaded());
    }

    @Test
    public void testMainActions() {
        System.out.println("Testing WaitingForReload ...");
        Board board = game.getBoard();
        List<Targetable> targetableList = new ArrayList<>();
        List<PlayerAction> playerActions = new ArrayList<>();
        PlayerAction reloadAction;

        ControllerState waitingForMainActions = new WaitingForMainActions();


    }

    public PowerUp getPowerUp(String cardName) {
        Deck<PowerUp> deck = DirectDeserializers.deserialzerPowerUpDeck();
        PowerUp card = deck.drawCard();

        while(!card.getName().equals(cardName)) {
            card = deck.drawCard();
        }
        //System.out.println(card.getName());
        return card;
    }

    public Weapon getWeapon(String cardName) {
        Deck<Weapon> deck = DirectDeserializers.deserialzerWeaponDeck();
        Weapon card = deck.drawCard();

        while(!card.getName().equals(cardName)) {
            card = deck.drawCard();
        }

        return card;
    }


}