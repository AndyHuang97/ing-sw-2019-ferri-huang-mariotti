package it.polimi.se2019.server.controller;

import it.polimi.se2019.client.util.Constants;
import it.polimi.se2019.server.actions.ActionUnit;
import it.polimi.se2019.server.cards.ammocrate.AmmoCrate;
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
        game.setCurrentPlayerNotify(p1);
        PowerUp bluePowerUp = getPowerUp("Blue_TargetingScope");
        PowerUp redPowerUp = getPowerUp("Red_TargetingScope");
        PowerUp yellowPowerUp = getPowerUp("Yellow_TargetingScope");

        playerActions = new ArrayList<>();
        playerActions.add(new NoOperation(game, p1));
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
        game.setCurrentPlayerNotify(p1);
        p1.getCharacterState().addWeapon(weapon);
        weapon.setLoaded(false);

        System.out.println("1) NOP, weapon will not be loaded, no one died");
        reloadAction = new NoOperation(game, p1);
        p2.getCharacterState().setFirstSpawn(false);
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
        game.setCurrentPlayerNotify(p1);
        p1.getCharacterState().setFirstSpawn(false);
        p3.getCharacterState().addDamage(PlayerColor.BLUE, 11, game);
        p4.getCharacterState().addDamage(PlayerColor.BLUE, 11, game);
        newState = waitingForReload.nextState(playerActions, game, p1);
        assertEquals(p3.getId(), game.getCurrentPlayer().getId());
        assertEquals(WaitingForRespawn.class, newState.getClass());
        assertFalse(weapon.isLoaded());

        System.out.println("3) Relaod weapon, no one died.");
        playerActions.clear();
        game.setCurrentPlayerNotify(p1);
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
        game.setCurrentPlayerNotify(p1);
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
        game.setCurrentPlayerNotify(p1);
        reloadAction = new MovePlayerAction(game, p1);
        playerActions.add(reloadAction);    // adding action
        newState = waitingForReload.nextState(playerActions, game, p1);
        assertEquals(waitingForReload, newState);
        assertEquals(p1.getId(), game.getCurrentPlayer().getId());

        System.out.println("6) Reload weapon failed. Weapon was already loaded");
        playerActions.clear();
        game.setCurrentPlayerNotify(p1);
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
    public void testMainActions() throws UnpackingException {
        System.out.println("Testing WaitingForMainActions ...");
        Board board = game.getBoard();
        List<Targetable> targetableList = new ArrayList<>();
        List<PlayerAction> playerActions = new ArrayList<>();
        PlayerAction action;
        ControllerState newState;

        ControllerState waitingForMainActions = new WaitingForMainActions();
        p1.getCharacterState().addDamage(PlayerColor.GREEN, 6, game);
        p1.getCharacterState().getPossibleActions(game.isFrenzy())
                .forEach(c -> {
                    //System.out.println(c.toString());
                    //c.getAction().forEach(playerAction -> System.out.println(playerAction.getId()+" "+playerAction.getAmount()));
                });

        System.out.println("1) Fail: action was not available");
        game.setCurrentPlayerNotify(p1);
        p1.getCharacterState().setTile(board.getTile(0,0));
        action = new MovePlayerAction(game, p1);
        targetableList.add(board.getTile(3,2));
        action.unpack(targetableList);
        playerActions.add(action);
        newState = waitingForMainActions.nextState(playerActions, game, p1);
        assertEquals(p1.getId(), game.getCurrentPlayer().getId());
        assertEquals(waitingForMainActions, newState);

        System.out.println("2) Fail: action available, but invalid input");
        Weapon w1 = getWeapon("Lock_Rifle");
        playerActions.clear();
        action = new ShootWeaponSelection(game, p1);
        targetableList = new ArrayList<>();
        targetableList.add(w1);
        action.unpack(targetableList);
        playerActions.add(action);
        newState = waitingForMainActions.nextState(playerActions, game ,p1);
        assertEquals(p1.getId(), game.getCurrentPlayer().getId());
        assertEquals(waitingForMainActions, newState);

        System.out.println("3) Success, ShootWeaponSelection ");
        game.setCurrentPlayerNotify(p1);
        game.setFrenzy(false);
        playerActions.clear();
        action = new ShootWeaponSelection(game, p1);
        p1.getCharacterState().addWeapon(w1);
        targetableList = new ArrayList<>();
        targetableList.add(w1);
        action.unpack(targetableList);
        playerActions.add(action);
        newState = waitingForMainActions.nextState(playerActions, game ,p1);
        assertEquals(p1.getId(), game.getCurrentPlayer().getId());
        assertEquals(WaitingForEffects.class, newState.getClass());

        System.out.println("4) Success, MovePlayerSelection");
        game.setCurrentPlayerNotify(p1);
        game.setFrenzy(false);
        waitingForMainActions = new WaitingForMainActions();
        playerActions.clear();
        p1.getCharacterState().setTile(board.getTile(0,0));
        action = new MovePlayerAction(game, p1);
        targetableList = new ArrayList<>();
        targetableList.add(board.getTile(0,1));
        action.unpack(targetableList);
        playerActions.add(action);
        newState = waitingForMainActions.nextState(playerActions, game, p1);
        assertEquals(p1.getId(), game.getCurrentPlayer().getId());
        assertEquals(waitingForMainActions, newState);

        System.out.println("4-bis) Success, MovePlayerSelection and GrabPlayerAction");
        game.setCurrentPlayerNotify(p1);
        game.setFrenzy(false);
        waitingForMainActions = new WaitingForMainActions();
        playerActions.clear();
        p1.getCharacterState().setTile(board.getTile(0,0));
        p1.getCharacterState().getAmmoBag().put(AmmoColor.RED,0);
        p1.getCharacterState().getPowerUpBag().clear();
        action = new MovePlayerAction(game, p1);
        targetableList = new ArrayList<>();
        targetableList.add(board.getTile(0,1)); // must be same as  the tile
        action.unpack(targetableList);
        playerActions.add(action);
        action = new GrabPlayerAction(game, p1);
        targetableList = new ArrayList<>();
        AmmoCrate ammoCrate = getAmmoCrate("1_PowerUp_2_Red");
        board.getTile(0,1).setAmmoCrate(ammoCrate);
        targetableList.add(ammoCrate);
        action.unpack(targetableList);
        playerActions.add(action);
        newState = waitingForMainActions.nextState(playerActions, game, p1);
        assertEquals(p1.getId(), game.getCurrentPlayer().getId());
        assertEquals(waitingForMainActions, newState);
        assertEquals(1, p1.getCharacterState().getPowerUpBag().size());
        assertEquals(0,p1.getCharacterState().getAmmoBag().get(AmmoColor.BLUE).intValue());
        assertEquals(0,p1.getCharacterState().getAmmoBag().get(AmmoColor.YELLOW).intValue());
        assertEquals(2,p1.getCharacterState().getAmmoBag().get(AmmoColor.RED).intValue());

        System.out.println("5) Success, NOP -> go to next player (not spawned)");
        game.setCurrentPlayerNotify(p1);
        game.setFrenzy(false);
        waitingForMainActions = new WaitingForMainActions();
        playerActions.clear();
        p1.getCharacterState().setFirstSpawn(false);
        action = new NoOperation(game, p1);
        targetableList = new ArrayList<>();
        action.unpack(targetableList);
        playerActions.add(action);
        newState = waitingForMainActions.nextState(playerActions, game, p1);
        assertEquals(p2.getId(), game.getCurrentPlayer().getId());
        assertEquals(WaitingForRespawn.class, newState.getClass());

        System.out.println("5-bis) Success, NOP -> go to next player(spawned)");
        game.setCurrentPlayerNotify(p1);
        game.setFrenzy(false);
        waitingForMainActions = new WaitingForMainActions();
        playerActions.clear();
        p1.getCharacterState().setFirstSpawn(false);
        p2.getCharacterState().setFirstSpawn(false);
        action = new NoOperation(game, p1);
        targetableList = new ArrayList<>();
        action.unpack(targetableList);
        playerActions.add(action);
        newState = waitingForMainActions.nextState(playerActions, game, p1);
        assertEquals(p2.getId(), game.getCurrentPlayer().getId());
        assertEquals(WaitingForMainActions.class, newState.getClass());

        System.out.println("6) Success, 2 actions -> go to next player");
        game.setCurrentPlayerNotify(p1);
        game.setFrenzy(false);
        waitingForMainActions = new WaitingForMainActions();
        playerActions.clear();
        p1.getCharacterState().setFirstSpawn(false);
        p2.getCharacterState().setFirstSpawn(false);
        action = new MovePlayerAction(game, p1);
        targetableList = new ArrayList<>();
        targetableList.add(board.getTile(0,1));
        action.unpack(targetableList);
        playerActions.add(action);
        newState = waitingForMainActions.nextState(playerActions, game, p1);
        assertEquals(p1.getId(), game.getCurrentPlayer().getId());
        assertEquals(WaitingForMainActions.class, newState.getClass());
        action = new MovePlayerAction(game, p1);
        targetableList = new ArrayList<>();
        targetableList.add(board.getTile(0,2));
        action.unpack(targetableList);
        playerActions.add(action);
        newState = waitingForMainActions.nextState(playerActions, game, p1);
        assertEquals(p1.getId(), game.getCurrentPlayer().getId());
        assertEquals(WaitingForReload.class, newState.getClass());

        System.out.println("7) Someone was killed in frenzy");
        game.setCurrentPlayerNotify(p1);
        game.setFrenzy(true);
        p1.getCharacterState().setBeforeFrenzyActivator(false); // only one action possible
        waitingForMainActions = new WaitingForMainActions();
        playerActions.clear();
        p1.getCharacterState().setFirstSpawn(false);
        p1.getCharacterState().setTile(board.getTile(0,0));
        p2.getCharacterState().setFirstSpawn(false);
        action = new MovePlayerAction(game, p1);
        targetableList = new ArrayList<>();
        targetableList.add(board.getTile(0,1));
        action.unpack(targetableList);
        playerActions.add(action);
        p3.getCharacterState().addDamage(PlayerColor.BLUE, 11, game);
        newState = waitingForMainActions.nextState(playerActions, game, p1);
        assertEquals(p3.getId(), game.getCurrentPlayer().getId());
        assertEquals(WaitingForRespawn.class, newState.getClass());

        System.out.println("8) No one was killed in frenzy");
        game.setCurrentPlayerNotify(p1);
        game.setFrenzy(true);
        p1.getCharacterState().setBeforeFrenzyActivator(false); // only one action possible
        waitingForMainActions = new WaitingForMainActions();
        playerActions.clear();
        p1.getCharacterState().setFirstSpawn(false);
        p1.getCharacterState().setTile(board.getTile(0,0));
        p2.getCharacterState().setFirstSpawn(false);
        action = new MovePlayerAction(game, p1);
        targetableList = new ArrayList<>();
        targetableList.add(board.getTile(0,1));
        action.unpack(targetableList);
        playerActions.add(action);
        p3.getCharacterState().resetDamageBar();
        newState = waitingForMainActions.nextState(playerActions, game, p1);
        assertEquals(p2.getId(), game.getCurrentPlayer().getId());
        assertEquals(WaitingForMainActions.class, newState.getClass());

        System.out.println("8) Some actions left");
        game.setCurrentPlayerNotify(p1);
        p1.getCharacterState().setBeforeFrenzyActivator(true); // only one action possible
        game.setFrenzy(true);
        waitingForMainActions = new WaitingForMainActions();
        playerActions.clear();
        p1.getCharacterState().setFirstSpawn(false);
        p1.getCharacterState().setTile(board.getTile(0,0));
        p2.getCharacterState().setFirstSpawn(false);
        action = new MovePlayerAction(game, p1);
        targetableList = new ArrayList<>();
        targetableList.add(board.getTile(0,1));
        action.unpack(targetableList);
        playerActions.add(action);
        p3.getCharacterState().resetDamageBar();
        newState = waitingForMainActions.nextState(playerActions, game, p1);
        assertEquals(p1.getId(), game.getCurrentPlayer().getId());
        assertEquals(WaitingForMainActions.class, newState.getClass());

        System.out.println("9) Received a Teleporter powerUp");
        game.setCurrentPlayer(p1);
        p1.getCharacterState().setBeforeFrenzyActivator(true); // only one action possible
        game.setFrenzy(false);
        waitingForMainActions = new WaitingForMainActions();
        playerActions.clear();
        p1.getCharacterState().setFirstSpawn(false);
        p1.getCharacterState().setTile(board.getTile(0,0));
        action = new PowerUpAction(game, p1);
        PowerUp teleporter = getPowerUp("Red_Teleporter");
        p1.getCharacterState().getPowerUpBag().add(teleporter);
        targetableList = new ArrayList<>();
        targetableList.add(teleporter);
        targetableList.add(board.getTile(3,2));
        action.unpack(targetableList);
        playerActions.add(action);
        newState = waitingForMainActions.nextState(playerActions, game, p1);
        assertEquals(waitingForMainActions, newState);
        assertEquals(p1.getId(), game.getCurrentPlayer().getId());
        assertEquals(board.getTile(3,2),p1.getCharacterState().getTile());

        System.out.println("9) Received a Newton powerUp");
        game.setCurrentPlayer(p1);
        p1.getCharacterState().setBeforeFrenzyActivator(true); // only one action possible
        game.setFrenzy(false);
        waitingForMainActions = new WaitingForMainActions();
        playerActions.clear();
        p1.getCharacterState().setFirstSpawn(false);
        p1.getCharacterState().setTile(board.getTile(0,0));
        p2.getCharacterState().setFirstSpawn(false);
        p2.getCharacterState().setTile(board.getTile(0,0));
        action = new PowerUpAction(game, p1);
        PowerUp newton = getPowerUp("Red_Newton");
        p1.getCharacterState().getPowerUpBag().add(newton);
        targetableList = new ArrayList<>();
        targetableList.add(newton);
        targetableList.add(p2);
        targetableList.add(board.getTile(2,0));
        action.unpack(targetableList);
        playerActions.add(action);
        newState = waitingForMainActions.nextState(playerActions, game, p1);
        assertEquals(waitingForMainActions, newState);
        assertEquals(p1.getId(), game.getCurrentPlayer().getId());
        assertEquals(board.getTile(0,0),p1.getCharacterState().getTile());
        assertEquals(board.getTile(2,0),p2.getCharacterState().getTile());

    }

    @Test
    public void testEffects() throws UnpackingException {
        System.out.println("Testing WaitingForMainActions ...");
        Board board = game.getBoard();
        List<Targetable> targetableList = new ArrayList<>();
        List<PlayerAction> playerActions = new ArrayList<>();
        PlayerAction action;
        ControllerState newState;

        Weapon weapon = getWeapon("Lock_Rifle");
        ControllerState waitingForMainActions = new WaitingForMainActions();
        ControllerState waitingForEffects;

        System.out.println("1)NOP, and no actions performed");
        game.setCurrentPlayerNotify(p1);
        waitingForEffects = new WaitingForEffects(weapon, waitingForMainActions);
        playerActions.clear();
        p1.getCharacterState().setTile(board.getTile(0,0));
        game.getCumulativeDamageTargetSet().clear();
        game.getCumulativeDamageTargetSet().add(p2);
        game.getCumulativeDamageTargetSet().add(p3);
        action = new NoOperation(game, p1);
        targetableList = new ArrayList<>();
        playerActions.add(action);
        game.getCurrentActionUnitsList().clear();
        newState = waitingForEffects.nextState(playerActions, game, p1);
        assertEquals(p1.getId(), game.getCurrentPlayer().getId());
        assertTrue(weapon.isLoaded());
        assertEquals(waitingForMainActions, newState);

        System.out.println("1-bis)NOP, an action action was performed -> unload the weapon");
        game.setCurrentPlayerNotify(p1);
        waitingForEffects = new WaitingForEffects(weapon, waitingForMainActions);
        playerActions.clear();
        p1.getCharacterState().setTile(board.getTile(0,0));
        game.getCumulativeDamageTargetSet().clear();
        game.getCumulativeDamageTargetSet().add(p2);
        game.getCumulativeDamageTargetSet().add(p3);
        action = new NoOperation(game, p1);
        targetableList = new ArrayList<>();
        playerActions.add(action);
        game.getCurrentActionUnitsList().clear();
        game.getCurrentActionUnitsList().add(new ActionUnit(true, "", null, null, 0,0,false));
        newState = waitingForEffects.nextState(playerActions, game, p1);
        assertEquals(p1.getId(), game.getCurrentPlayer().getId());
        assertFalse(weapon.isLoaded());
        assertEquals(waitingForMainActions, newState);

        System.out.println("2) Invalid action");
        game.setCurrentPlayerNotify(p1);
        waitingForEffects = new WaitingForEffects(weapon, waitingForMainActions);
        playerActions.clear();
        p1.getCharacterState().setTile(board.getTile(0,0));
        game.getCumulativeDamageTargetSet().clear();
        game.getCumulativeDamageTargetSet().add(p2);
        game.getCumulativeDamageTargetSet().add(p3);
        action = new GrabPlayerAction(game, p1);
        targetableList = new ArrayList<>();
        playerActions.add(action);
        game.getCurrentActionUnitsList().clear();
        game.getCurrentActionUnitsList().add(new ActionUnit(true, "", null, null, 0,0,false));
        newState = waitingForEffects.nextState(playerActions, game, p1);
        assertEquals(waitingForEffects, newState);

        System.out.println("3) ShootPlayerAction failed check, weapon was not loaded");
        game.setCurrentPlayerNotify(p1);
        waitingForEffects = new WaitingForEffects(weapon, waitingForMainActions);
        playerActions.clear();
        p1.getCharacterState().setTile(board.getTile(0,0));
        game.getCumulativeDamageTargetSet().clear();
        game.getCumulativeDamageTargetSet().add(p2);
        game.getCumulativeDamageTargetSet().add(p3);
        action = new ShootPlayerAction(game, p1);
        targetableList = new ArrayList<>();
        targetableList.add(weapon);
        p1.getCharacterState().addWeapon(weapon);
        weapon.setLoaded(false);
        ActionUnit basicMode = weapon.getActionUnitList().stream()
                .filter(au -> au.getName().equals("Basic Mode")).findFirst().orElse(null);
        System.out.println(basicMode.getName());
        targetableList.add(basicMode);
        targetableList.add(p2);
        p2.getCharacterState().setTile(board.getTile(0,1));
        action.unpack(targetableList);
        assertEquals(weapon, action.getCard());
        playerActions.add(action);
        game.getCurrentActionUnitsList().clear();
        game.getCurrentActionUnitsList().add(new ActionUnit(true, "", null, null, 0,0,false));
        newState = waitingForEffects.nextState(playerActions, game, p1);
        assertEquals(p1.getId(), game.getCurrentPlayer().getId());
        assertFalse(weapon.isLoaded());
        assertEquals(waitingForEffects, newState);
        p1.getCharacterState().removeWeapon(weapon);

        System.out.println("4.1) ShootPlayerAction with damage, more optional effects");
        weapon = getWeapon("Lock_Rifle");
        game.setCurrentPlayerNotify(p1);
        waitingForEffects = new WaitingForEffects(weapon, waitingForMainActions);
        playerActions.clear();
        p1.getCharacterState().setTile(board.getTile(0,0));
        game.getCumulativeDamageTargetSet().clear();
        game.getCurrentActionUnitsList().clear();
        action = new ShootPlayerAction(game, p1);
        targetableList = new ArrayList<>();
        targetableList.add(weapon);
        p1.getCharacterState().addWeapon(weapon);
        p1.getCharacterState().getPowerUpBag().clear();
        p2.getCharacterState().getPowerUpBag().clear();
        weapon.setLoaded(true);
        basicMode = weapon.getActionUnitList().stream()
                .filter(au -> au.getName().equals("Basic Mode")).findFirst().orElse(null);
        System.out.println(basicMode.getName());
        targetableList.add(basicMode);
        targetableList.add(p2);
        p2.getCharacterState().setTile(board.getTile(0,1));
        action.unpack(targetableList);
        assertEquals(weapon, action.getCard());
        playerActions.add(action);
        newState = waitingForEffects.nextState(playerActions, game, p1);
        assertEquals(p1.getId(), game.getCurrentPlayer().getId());
        assertEquals(2, p2.getCharacterState().getDamageBar().size());
        assertEquals(1, p2.getCharacterState().getMarkerBar().get(PlayerColor.BLUE).intValue());
        assertEquals(0, game.getCumulativeDamageTargetSet().size());
        p2.getCharacterState().resetDamageBar();
        p2.getCharacterState().resetMarkerBar();
        assertEquals(waitingForEffects, newState);
        p1.getCharacterState().removeWeapon(weapon);

        System.out.println("4.2) ShootPlayerAction with NO damage, no more optional effects");
        weapon = getWeapon("Lock_Rifle");
        game.setCurrentPlayerNotify(p1);
        waitingForEffects = new WaitingForEffects(weapon, waitingForMainActions);
        playerActions.clear();
        p1.getCharacterState().setTile(board.getTile(0,0));
        action = new ShootPlayerAction(game, p1);
        targetableList = new ArrayList<>();
        targetableList.add(weapon);
        p1.getCharacterState().addWeapon(weapon);
        p1.getCharacterState().getPowerUpBag().clear();
        p1.getCharacterState().getAmmoBag().put(AmmoColor.RED,3);
        p2.getCharacterState().getPowerUpBag().clear();
        weapon.setLoaded(true);
        ActionUnit secondLock = weapon.getOptionalEffectList().stream()
                .filter(au -> au.getName().equals("Second Lock Effect")).findFirst().orElse(null);
        System.out.println(secondLock.getName());
        targetableList.add(secondLock);
        p3.getCharacterState().setTile(board.getTile(0,1));
        targetableList.add(p3);
        action.unpack(targetableList);
        assertEquals(weapon, action.getCard());
        playerActions.add(action);
        game.getCurrentActionUnitsList().clear();
        game.getCurrentActionUnitsList().add(basicMode);
        game.getCurrentActionUnitsList().forEach(au -> System.out.println("CurrentActionUnitList: "+au.getName()));
        newState = waitingForEffects.nextState(playerActions, game, p1);
        assertEquals(p1.getId(), game.getCurrentPlayer().getId());
        assertEquals(waitingForMainActions, newState);
        assertEquals(0, p3.getCharacterState().getDamageBar().size());
        assertEquals(1, p3.getCharacterState().getMarkerBar().get(PlayerColor.BLUE).intValue());
        assertEquals(0, game.getCumulativeDamageTargetSet().size());
        p3.getCharacterState().resetDamageBar();
        p3.getCharacterState().resetMarkerBar();
        p1.getCharacterState().removeWeapon(weapon);
/*
        System.out.println("4.3) ShootPlayerAction with NO damage, no more optional effects, no more actions " +
                "-> goes to next MainAction");
        p1.getCharacterState().setBeforeFrenzyActivator(false);
        game.setFrenzy(true);
        ((WaitingForMainActions)waitingForMainActions).updateCounter();
        waitingForEffects = new WaitingForEffects(weapon, waitingForMainActions);
        p1.getCharacterState().getAmmoBag().put(AmmoColor.RED,2);
        newState = waitingForEffects.nextState(playerActions, game, p1);
        assertEquals(WaitingForMainActions.class, newState.getClass());


        System.out.println("4.4) ShootPlayerActionl with NO damage, no more optional effects, no more actions " +
                "-> goes to next respawn");
        p1.getCharacterState().setBeforeFrenzyActivator(false);
        game.setFrenzy(false);
        //waitingForMainActions = new WaitingForMainActions();
        ((WaitingForMainActions)waitingForMainActions).updateCounter();
        ((WaitingForMainActions)waitingForMainActions).updateCounter();
        waitingForEffects = new WaitingForEffects(weapon, waitingForMainActions);
        p1.getCharacterState().getAmmoBag().put(AmmoColor.RED,1);
        p1.getCharacterState().setFirstSpawn(false);
        p2.getCharacterState().addDamage(PlayerColor.BLUE,11,game);
        newState = waitingForEffects.nextState(playerActions, game, p1);
        assertEquals(WaitingForReload.class, newState.getClass());
        //2assertEquals(WaitingForRespawn.class, newState.getClass());
        //2assertEquals(p2, game.getCurrentPlayer());
        p2.getCharacterState().resetDamageBar();

 */


        System.out.println("5) ShootPlayerAction with damage, no more optional effects");
        weapon = getWeapon("Whisper");
        game.setCurrentPlayerNotify(p1);
        game.setFrenzy(false);
        waitingForEffects = new WaitingForEffects(weapon, waitingForMainActions);
        playerActions.clear();
        p1.getCharacterState().setTile(board.getTile(0,0));
        game.getCumulativeDamageTargetSet().clear();
        action = new ShootPlayerAction(game, p1);
        targetableList = new ArrayList<>();
        targetableList.add(weapon);
        p1.getCharacterState().addWeapon(weapon);
        p1.getCharacterState().getPowerUpBag().clear();
        p2.getCharacterState().getPowerUpBag().clear();
        weapon.setLoaded(true);
        basicMode = weapon.getActionUnitList().stream()
                .filter(au -> au.getName().equals("Basic Mode")).findFirst().orElse(null);
        System.out.println(basicMode.getName());
        targetableList.add(basicMode);
        targetableList.add(p2);
        p2.getCharacterState().setTile(board.getTile(2,0));
        action.unpack(targetableList);
        assertEquals(weapon, action.getCard());
        playerActions.add(action);
        game.getCurrentActionUnitsList().clear();
        newState = waitingForEffects.nextState(playerActions, game, p1);
        assertEquals(p1.getId(), game.getCurrentPlayer().getId());
        assertEquals(3, p2.getCharacterState().getDamageBar().size());
        assertEquals(1, p2.getCharacterState().getMarkerBar().get(PlayerColor.BLUE).intValue());
        assertEquals(0, game.getCumulativeDamageTargetSet().size());
        p2.getCharacterState().resetDamageBar();
        p2.getCharacterState().resetMarkerBar();
        assertEquals(waitingForMainActions, newState);
        p1.getCharacterState().removeWeapon(weapon);

        System.out.println("6) ShootPlayerAction with NO damage, more optional effects left");
        weapon = getWeapon("Plasma_Gun");
        game.setCurrentPlayerNotify(p1);
        waitingForEffects = new WaitingForEffects(weapon, waitingForMainActions);
        playerActions.clear();
        p1.getCharacterState().setTile(board.getTile(0,0));
        action = new ShootPlayerAction(game, p1);
        targetableList = new ArrayList<>();
        targetableList.add(weapon);
        p1.getCharacterState().addWeapon(weapon);
        p1.getCharacterState().getPowerUpBag().clear();
        p1.getCharacterState().getAmmoBag().put(AmmoColor.RED,1);
        p2.getCharacterState().getPowerUpBag().clear();
        weapon.setLoaded(true);
        ActionUnit phaseGlide = weapon.getOptionalEffectList().stream()
                .filter(au -> au.getName().equals("Phase Glide Effect")).findFirst().orElse(null);
        System.out.println(phaseGlide.getName());
        targetableList.add(phaseGlide);
        targetableList.add(board.getTile(0,1));
        action.unpack(targetableList);
        assertEquals(weapon, action.getCard());
        playerActions.add(action);
        game.getCurrentActionUnitsList().clear();
        game.getCurrentActionUnitsList().add(basicMode);
        game.getCurrentActionUnitsList().forEach(au -> System.out.println("CurrentActionUnitList: "+au.getName()));
        newState = waitingForEffects.nextState(playerActions, game, p1);
        assertEquals(p1.getId(), game.getCurrentPlayer().getId());
        assertEquals(0, game.getCumulativeDamageTargetSet().size());
        p2.getCharacterState().resetDamageBar();
        p2.getCharacterState().resetMarkerBar();
        assertEquals(waitingForEffects, newState);
        p1.getCharacterState().removeWeapon(weapon);

        System.out.println("7) ShootPlayerAction with damage, attacker with Targeting Scope");
        weapon = getWeapon("Lock_Rifle");
        game.setCurrentPlayerNotify(p1);
        waitingForEffects = new WaitingForEffects(weapon, waitingForMainActions);
        playerActions.clear();
        p1.getCharacterState().setTile(board.getTile(0,0));
        game.getCumulativeDamageTargetSet().clear();
        game.getCurrentActionUnitsList().clear();
        action = new ShootPlayerAction(game, p1);
        targetableList = new ArrayList<>();
        targetableList.add(weapon);
        p1.getCharacterState().addWeapon(weapon);
        PowerUp powerUp = getPowerUp("Red_TargetingScope");
        p1.getCharacterState().getPowerUpBag().clear();
        p1.getCharacterState().addPowerUp(powerUp);
        weapon.setLoaded(true);
        basicMode = weapon.getActionUnitList().stream()
                .filter(au -> au.getName().equals("Basic Mode")).findFirst().orElse(null);
        System.out.println(basicMode.getName());
        targetableList.add(basicMode);
        targetableList.add(p2);
        p2.getCharacterState().setTile(board.getTile(0,1));
        action.unpack(targetableList);
        assertEquals(weapon, action.getCard());
        playerActions.add(action);
        newState = waitingForEffects.nextState(playerActions, game, p1);
        assertEquals(p1.getId(), game.getCurrentPlayer().getId());
        assertEquals(WaitingForPowerUps.class, newState.getClass());
        p1.getCharacterState().removeWeapon(weapon);


    }

    @Test
    public void testPowerUps() throws UnpackingException {
        System.out.println("Testing WaitingForPowerUps ...");
        Board board = game.getBoard();
        List<Targetable> targetableList = new ArrayList<>();
        List<PlayerAction> playerActions = new ArrayList<>();
        PlayerAction action;
        ControllerState newState;

        WaitingForPowerUps waitingForPowerUps;
        ControllerState waitingForEffects;
        WaitingForMainActions waitingForMainActions = new WaitingForMainActions();
        Weapon weapon = getWeapon("Lock_Rifle");
        game.setCurrentPlayerNotify(p1);
        waitingForEffects = new WaitingForEffects(weapon, waitingForMainActions);
        playerActions.clear();
        p1.getCharacterState().setTile(board.getTile(0,0));
        game.getCumulativeDamageTargetSet().clear();
        game.getCurrentActionUnitsList().clear();
        action = new ShootPlayerAction(game, p1);
        targetableList = new ArrayList<>();
        targetableList.add(weapon);
        p1.getCharacterState().addWeapon(weapon);
        p1.getCharacterState().getPowerUpBag().clear();
        p2.getCharacterState().getPowerUpBag().clear();
        weapon.setLoaded(true);
        ActionUnit basicMode = weapon.getActionUnitList().stream()
                .filter(au -> au.getName().equals("Basic Mode")).findFirst().orElse(null);
        System.out.println(basicMode.getName());
        targetableList.add(basicMode);
        targetableList.add(p2);
        p2.getCharacterState().setTile(board.getTile(0,1));
        action.unpack(targetableList);
        assertEquals(weapon, action.getCard());
        playerActions.add(action);
        newState = waitingForEffects.nextState(playerActions, game, p1);
        assertEquals(p1.getId(), game.getCurrentPlayer().getId());
        assertEquals(2, p2.getCharacterState().getDamageBar().size());
        assertEquals(1, p2.getCharacterState().getMarkerBar().get(PlayerColor.BLUE).intValue());
        assertEquals(0, game.getCumulativeDamageTargetSet().size());
        p2.getCharacterState().resetDamageBar();
        p2.getCharacterState().resetMarkerBar();
        assertEquals(waitingForEffects, newState);
        p1.getCharacterState().removeWeapon(weapon);


        System.out.println("1) Invalid input for an invalid expected powerup");
        game.setCurrentPlayerNotify(p1);
        p1.getCharacterState().setBeforeFrenzyActivator(true); // only one action possible
        game.setFrenzy(true);
        waitingForPowerUps = new WaitingForPowerUps("", waitingForEffects);
        playerActions.clear();
        p1.getCharacterState().setFirstSpawn(false);
        p1.getCharacterState().setTile(board.getTile(0,0));
        p2.getCharacterState().setFirstSpawn(false);
        action = new NoOperation(game, p1);
        targetableList = new ArrayList<>();
        targetableList.add(board.getTile(0,1));
        action.unpack(targetableList);
        playerActions.add(action);
        newState = waitingForPowerUps.nextState(playerActions, game, p1);
        assertEquals(p1.getId(), game.getCurrentPlayer().getId());
        assertEquals(waitingForPowerUps, newState);

        System.out.println("2) NOP for Targeting Scope");
        game.setCurrentPlayerNotify(p1);
        p1.getCharacterState().setBeforeFrenzyActivator(true); // only one action possible
        game.setFrenzy(true);
        waitingForPowerUps = new WaitingForPowerUps(Constants.TARGETING_SCOPE, waitingForEffects);
        playerActions.clear();
        p1.getCharacterState().setFirstSpawn(false);
        p1.getCharacterState().setTile(board.getTile(0,0));
        p2.getCharacterState().setFirstSpawn(false);
        action = new NoOperation(game, p1);
        targetableList = new ArrayList<>();
        targetableList.add(board.getTile(0,1));
        action.unpack(targetableList);
        playerActions.add(action);
        newState = waitingForPowerUps.nextState(playerActions, game, p1);
        assertEquals(p1.getId(), game.getCurrentPlayer().getId());
        assertEquals(waitingForEffects, newState);


        System.out.println("2-bis) Targeting Scope, ");
        game.setCurrentPlayerNotify(p1);
        p1.getCharacterState().setBeforeFrenzyActivator(true); // only one action possible
        game.setFrenzy(true);
        waitingForPowerUps = new WaitingForPowerUps(Constants.TARGETING_SCOPE, waitingForEffects);
        playerActions.clear();
        p1.getCharacterState().setFirstSpawn(false);
        p1.getCharacterState().setTile(board.getTile(0,0));
        p2.getCharacterState().setFirstSpawn(false);
        p1.getCharacterState().getAmmoBag().put(AmmoColor.RED,1);
        p1.getCharacterState().getAmmoBag().put(AmmoColor.BLUE,1);
        game.getCumulativeDamageTargetSet().add(p2);
        action = new PowerUpAction(game, p1);
        targetableList = new ArrayList<>();
        PowerUp blueTargetingScope = getPowerUp("Blue_TargetingScope");
        PowerUp redTargetingScope = getPowerUp("Red_TargetingScope");
        p1.getCharacterState().getPowerUpBag().add(blueTargetingScope);
        p1.getCharacterState().getPowerUpBag().add(redTargetingScope);
        targetableList.add(blueTargetingScope);
        targetableList.add(p2);
        targetableList.add(AmmoColor.BLUE);
        targetableList.add(redTargetingScope);
        targetableList.add(p2);
        targetableList.add(AmmoColor.RED);
        action.unpack(targetableList);
        playerActions.add(action);
        p2.getCharacterState().resetDamageBar();
        newState = waitingForPowerUps.nextState(playerActions, game, p1);
        assertEquals(p1.getId(), game.getCurrentPlayer().getId());
        assertEquals(waitingForEffects, newState);
        assertEquals(2, p2.getCharacterState().getDamageBar().size());
        assertEquals(0, p1.getCharacterState().getAmmoBag().get(AmmoColor.RED).intValue());
        assertEquals(0, p1.getCharacterState().getAmmoBag().get(AmmoColor.BLUE).intValue());
        assertEquals(0, p1.getCharacterState().getPowerUpBag().size());

        System.out.println("3) Incorrect powerUp, not a Targeting scope");
        game.setCurrentPlayerNotify(p1);
        p1.getCharacterState().setBeforeFrenzyActivator(true); // only one action possible
        game.setFrenzy(true);
        waitingForPowerUps = new WaitingForPowerUps(Constants.TARGETING_SCOPE, waitingForEffects);
        playerActions.clear();
        p1.getCharacterState().setFirstSpawn(false);
        p1.getCharacterState().setTile(board.getTile(0,0));
        p2.getCharacterState().setFirstSpawn(false);
        game.getCumulativeDamageTargetSet().clear();
        game.getCumulativeDamageTargetSet().add(p2);
        action = new PowerUpAction(game, p1);
        targetableList = new ArrayList<>();
        PowerUp powerUp = getPowerUp("Blue_Teleporter");
        p1.getCharacterState().getPowerUpBag().clear();
        p1.getCharacterState().getPowerUpBag().add(powerUp);
        targetableList.add(powerUp);
        targetableList.add(p2);
        action.unpack(targetableList);
        playerActions.add(action);
        newState = waitingForPowerUps.nextState(playerActions, game, p1);
        assertEquals(p1.getId(), game.getCurrentPlayer().getId());
        assertEquals(waitingForPowerUps, newState);


        System.out.println("4) Incorrect powerUp. Expecting a Tagback, but wasnt");
        game.setCurrentPlayerNotify(p1);
        p1.getCharacterState().setBeforeFrenzyActivator(true); // only one action possible
        game.setFrenzy(true);
        waitingForPowerUps = new WaitingForPowerUps(Constants.TAGBACK_GRENADE, waitingForEffects);
        playerActions.clear();
        p1.getCharacterState().setFirstSpawn(false);
        p1.getCharacterState().setTile(board.getTile(0,0));
        p2.getCharacterState().setFirstSpawn(false);
        game.getCumulativeDamageTargetSet().clear();
        game.getCumulativeDamageTargetSet().add(p2);
        action = new PowerUpAction(game, p1);
        targetableList = new ArrayList<>();
        powerUp = getPowerUp("Blue_Teleporter");
        p1.getCharacterState().getPowerUpBag().clear();
        p1.getCharacterState().getPowerUpBag().add(powerUp);
        targetableList.add(powerUp);
        targetableList.add(p2);
        action.unpack(targetableList);
        playerActions.add(action);
        newState = waitingForPowerUps.nextState(playerActions, game, p1);
        assertEquals(p1.getId(), game.getCurrentPlayer().getId());
        assertEquals(waitingForPowerUps, newState);

        System.out.println("5) Correct Tagback Grenade, no more people left to ask");
        game.setCurrentPlayerNotify(p1);
        p1.getCharacterState().setBeforeFrenzyActivator(true); // only one action possible
        game.setFrenzy(true);
        waitingForPowerUps = new WaitingForPowerUps(Constants.TAGBACK_GRENADE, waitingForEffects);
        playerActions.clear();
        p1.getCharacterState().setFirstSpawn(false);
        p1.getCharacterState().setTile(board.getTile(0,0));
        p2.getCharacterState().setFirstSpawn(false);
        game.getCumulativeDamageTargetSet().clear();
        game.getCumulativeDamageTargetSet().add(p2);
        action = new PowerUpAction(game, p2);
        targetableList = new ArrayList<>();
        powerUp = getPowerUp("Red_TagbackGrenade");
        PowerUp powerUp1 = getPowerUp("Yellow_TagbackGrenade");
        p2.getCharacterState().getPowerUpBag().clear();
        p2.getCharacterState().getPowerUpBag().add(powerUp);
        p2.getCharacterState().getPowerUpBag().add(powerUp1);
        p2.getCharacterState().setTile(board.getTile(0,1));
        p3.getCharacterState().setTile(board.getTile(0,1));
        PowerUp powerUp2 = getPowerUp("Blue_TagbackGrenade");
        p3.getCharacterState().getPowerUpBag().add(powerUp2);
        targetableList.add(powerUp);
        action.unpack(targetableList);
        playerActions.add(action);
        waitingForPowerUps.getPlayerStack().push(p1);
        waitingForPowerUps.getPlayerStack().push(p2);
        waitingForPowerUps.getAlreadyAskedPlayers().add(p2);
        game.getCurrentActionUnitsList().add(weapon.getOptionalEffectList().get(0));
        newState = waitingForPowerUps.nextState(playerActions, game, p2);
        assertEquals(p1.getId(), game.getCurrentPlayer().getId());
        assertEquals(waitingForMainActions, newState);

        System.out.println("6) Correct Tagback Grenade, more people left to ask for powerup");
        game.setCurrentPlayerNotify(p1);
        p1.getCharacterState().setBeforeFrenzyActivator(true); // only one action possible
        game.setFrenzy(true);
        waitingForPowerUps = new WaitingForPowerUps(Constants.TAGBACK_GRENADE, waitingForEffects);
        playerActions.clear();
        p1.getCharacterState().setFirstSpawn(false);
        p1.getCharacterState().setTile(board.getTile(0,0));
        p2.getCharacterState().setFirstSpawn(false);
        game.getCumulativeDamageTargetSet().clear();
        game.getCumulativeDamageTargetSet().add(p2);
        game.getCumulativeDamageTargetSet().add(p3);
        action = new PowerUpAction(game, p2);
        targetableList = new ArrayList<>();
        powerUp = getPowerUp("Red_TagbackGrenade");
        powerUp1 = getPowerUp("Yellow_TagbackGrenade");
        p2.getCharacterState().getPowerUpBag().clear();
        p2.getCharacterState().getPowerUpBag().add(powerUp);
        p2.getCharacterState().getPowerUpBag().add(powerUp1);
        p2.getCharacterState().setTile(board.getTile(0,1));
        powerUp2 = getPowerUp("Blue_TagbackGrenade");
        p3.getCharacterState().setTile(board.getTile(0,1));
        p3.getCharacterState().getPowerUpBag().clear();
        p3.getCharacterState().getPowerUpBag().add(powerUp2);
        targetableList.add(powerUp);
        action.unpack(targetableList);
        playerActions.add(action);
        waitingForPowerUps.getPlayerStack().push(p1);
        waitingForPowerUps.getPlayerStack().push(p2);
        waitingForPowerUps.getAlreadyAskedPlayers().add(p2);
        newState = waitingForPowerUps.nextState(playerActions, game, p2);
        assertEquals(1, p2.getCharacterState().getPowerUpBag().size());
        assertEquals(p3.getId(), game.getCurrentPlayer().getId());
        assertEquals(waitingForPowerUps, newState);

        System.out.println("7) Correct Tagback Grenade, no more people left to ask and no optional effects left");
        game.setCurrentPlayerNotify(p1);
        waitingForPowerUps = new WaitingForPowerUps(Constants.TAGBACK_GRENADE, waitingForEffects);
        playerActions.clear();
        p1.getCharacterState().setFirstSpawn(false);
        p1.getCharacterState().setTile(board.getTile(0,0));
        p2.getCharacterState().setFirstSpawn(false);
        game.getCumulativeDamageTargetSet().clear();
        game.getCumulativeDamageTargetSet().add(p2);
        action = new PowerUpAction(game, p2);
        targetableList = new ArrayList<>();
        powerUp = getPowerUp("Red_TagbackGrenade");
        powerUp1 = getPowerUp("Yellow_TagbackGrenade");
        p2.getCharacterState().getPowerUpBag().clear();
        p2.getCharacterState().getPowerUpBag().add(powerUp);
        p2.getCharacterState().getPowerUpBag().add(powerUp1);
        p2.getCharacterState().setTile(board.getTile(0,1));
        p3.getCharacterState().setTile(board.getTile(0,1));
        powerUp2 = getPowerUp("Blue_TagbackGrenade");
        p3.getCharacterState().getPowerUpBag().add(powerUp2);
        targetableList.add(powerUp);
        targetableList.add(powerUp1);
        action.unpack(targetableList);
        playerActions.add(action);
        waitingForPowerUps.getPlayerStack().push(p1);
        waitingForPowerUps.getPlayerStack().push(p2);
        waitingForPowerUps.getAlreadyAskedPlayers().add(p2);
        game.getCurrentActionUnitsList().clear();
        game.getCurrentActionUnitsList().add(basicMode);
        game.getCurrentActionUnitsList().add(weapon.getOptionalEffectList().get(0)); // needed to leave no optional effects
        //game.getCurrentActionUnitsList().forEach(au -> System.out.println(au.getId()));
        p1.getCharacterState().resetMarkerBar();
        newState = waitingForPowerUps.nextState(playerActions, game, p2);
        assertEquals(2, p1.getCharacterState().getMarkerBar().get(p2.getColor()).intValue());
        assertEquals(p1.getId(), game.getCurrentPlayer().getId());
        assertEquals(waitingForMainActions, newState);


    }

    private PowerUp getPowerUp(String cardName) {
        Deck<PowerUp> deck = DirectDeserializers.deserialzerPowerUpDeck();
        PowerUp card = deck.drawCard();

        while(!card.getName().equals(cardName)) {
            card = deck.drawCard();
        }
        //System.out.println(card.getName());
        return card;
    }

    private Weapon getWeapon(String cardName) {
        Deck<Weapon> deck = DirectDeserializers.deserialzerWeaponDeck();
        Weapon card = deck.drawCard();

        while(!card.getName().equals(cardName)) {
            card = deck.drawCard();
        }

        return card;
    }

    private AmmoCrate getAmmoCrate(String cardName) {
        Deck<AmmoCrate> deck = DirectDeserializers.deserializeAmmoCrate();
        AmmoCrate card = deck.drawCard();

        while(!card.getName().equals(cardName)) {
            card = deck.drawCard();
        }

        return card;
    }


}