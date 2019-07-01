package it.polimi.se2019.server.net;

import it.polimi.se2019.server.cards.ammocrate.AmmoCrate;
import it.polimi.se2019.server.cards.powerup.PowerUp;
import it.polimi.se2019.server.cards.weapons.Weapon;
import it.polimi.se2019.server.deserialize.DirectDeserializers;
import it.polimi.se2019.server.games.Deck;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.player.CharacterState;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.games.player.PlayerColor;
import it.polimi.se2019.server.playerActions.PlayerAction;
import it.polimi.se2019.server.users.UserData;
import it.polimi.se2019.util.InternalMessage;
import it.polimi.se2019.util.NetMessage;
import it.polimi.se2019.util.Request;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static it.polimi.se2019.client.util.Constants.*;
import static org.junit.Assert.*;

public class CommandHandlerTest {

    CommandHandler commandHandler;
    Request request;
    Game game;
    Player p1, p2, p3, p4, p5;

    @Before
    public void setUp() {
        commandHandler = new CommandHandler();
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


    @Test
    public void testTile() {
        NetMessage netMessage;
        Map<String, List<String>> clientCommands = new HashMap<>();
        List<String> action = new ArrayList<>();
        action.add(MOVE);
        clientCommands.put(KEY_ORDER, action);
        List<String> tile = new ArrayList<>();
        tile.add("0");
        clientCommands.put(MOVE, tile);
        netMessage = new NetMessage(clientCommands);
        InternalMessage internalMessage = commandHandler.convertNetMessage(netMessage, game);
        assertEquals(game.getBoard().getTile(0,0), internalMessage.getCommands().get(MOVE).get(0));
        assertEquals(PlayerAction.MOVE, internalMessage.getCommands().get(KEY_ORDER).get(0));
    }

    @Test
    public void testAmmoCrate() {
        NetMessage netMessage;
        Map<String, List<String>> clientCommands = new HashMap<>();
        List<String> action = new ArrayList<>();
        action.add(GRAB);
        clientCommands.put(KEY_ORDER, action);
        AmmoCrate ammoCrate = getAmmoCrate("1_Yellow_2_Blue");
        game.getBoard().getTile(0,0).setAmmoCrate(ammoCrate);
        List<String> ammocrateList = new ArrayList<>();
        ammocrateList.add("1_Yellow_2_Blue");
        clientCommands.put(GRAB, ammocrateList);
        netMessage = new NetMessage(clientCommands);
        InternalMessage internalMessage = commandHandler.convertNetMessage(netMessage, game);
        assertEquals(ammoCrate, internalMessage.getCommands().get(GRAB).get(0));
        assertEquals(PlayerAction.GRAB, internalMessage.getCommands().get(KEY_ORDER).get(0));
    }

    @Test
    public void testPowerUp() {
        NetMessage netMessage;
        Map<String, List<String>> clientCommands = new HashMap<>();
        List<String> action = new ArrayList<>();
        action.add(POWERUP);
        clientCommands.put(KEY_ORDER, action);
        PowerUp powerUp = getPowerUp("Red_Teleporter");
        PowerUp newton = getPowerUp("Red_Newton");
        p1.getCharacterState().getPowerUpBag().clear();
        p1.getCharacterState().getPowerUpBag().add(powerUp);
        p1.getCharacterState().getPowerUpBag().add(newton);
        List<String> powerUpList = new ArrayList<>();
        powerUpList.add("Red_Teleporter");
        powerUpList.add("Red_Newton");
        clientCommands.put(POWERUP, powerUpList);
        netMessage = new NetMessage(clientCommands);
        InternalMessage internalMessage = commandHandler.convertNetMessage(netMessage, game);
        assertEquals(powerUp, internalMessage.getCommands().get(POWERUP).get(0));
        assertEquals(newton, internalMessage.getCommands().get(POWERUP).get(1));
        assertEquals(PlayerAction.POWERUP, internalMessage.getCommands().get(KEY_ORDER).get(0));
    }

    @Test
    public void testPlayer() {
        NetMessage netMessage;
        Map<String, List<String>> clientCommands = new HashMap<>();
        List<String> action = new ArrayList<>();
        action.add(SHOOT);
        clientCommands.put(KEY_ORDER, action);
        List<String> playerList = new ArrayList<>();
        playerList.add("P1");
        clientCommands.put(SHOOT, playerList);
        netMessage = new NetMessage(clientCommands);
        InternalMessage internalMessage = commandHandler.convertNetMessage(netMessage, game);
        assertEquals(p1, internalMessage.getCommands().get(SHOOT).get(0));
        assertEquals(PlayerAction.SHOOT, internalMessage.getCommands().get(KEY_ORDER).get(0));
    }

    @Test
    public void testWeapon() {
        // from player's hand
        NetMessage netMessage;
        Map<String, List<String>> clientCommands = new HashMap<>();
        List<String> action = new ArrayList<>();
        action.add(SHOOT_WEAPON);
        clientCommands.put(KEY_ORDER, action);
        Weapon weapon = getWeapon("Lock_Rifle");
        p1.getCharacterState().getWeaponBag().clear();
        p1.getCharacterState().getWeaponBag().add(weapon);
        List<String> weaponList = new ArrayList<>();
        weaponList.add("Lock_Rifle");
        clientCommands.put(SHOOT_WEAPON, weaponList);
        netMessage = new NetMessage(clientCommands);
        InternalMessage internalMessage = commandHandler.convertNetMessage(netMessage, game);
        assertEquals(weapon, internalMessage.getCommands().get(SHOOT_WEAPON).get(0));
        assertEquals(PlayerAction.SHOOT_WEAPON, internalMessage.getCommands().get(KEY_ORDER).get(0));
        // from spawnTile weapon
        clientCommands = new HashMap<>();
        action = new ArrayList<>();
        action.add(SHOOT_WEAPON);
        clientCommands.put(KEY_ORDER, action);
        Weapon heatseeker = getWeapon("Heatseeker");
        p1.getCharacterState().getWeaponBag().add(heatseeker);
        weaponList = new ArrayList<>();
        weaponList.add("Heatseeker");
        clientCommands.put(SHOOT_WEAPON, weaponList);
        netMessage = new NetMessage(clientCommands);
        internalMessage = commandHandler.convertNetMessage(netMessage, game);
        assertEquals(heatseeker, internalMessage.getCommands().get(SHOOT_WEAPON).get(0));
        assertEquals(PlayerAction.SHOOT_WEAPON, internalMessage.getCommands().get(KEY_ORDER).get(0));
    }

    @Test
    public void testActionUnit() {
        NetMessage netMessage;
        Map<String, List<String>> clientCommands = new HashMap<>();
        List<String> action = new ArrayList<>();
        action.add(SHOOT);
        clientCommands.put(KEY_ORDER, action);
        Weapon weapon = getWeapon("Lock_Rifle");
        p1.getCharacterState().getWeaponBag().clear();
        p1.getCharacterState().getWeaponBag().add(weapon);
        List<String> weaponList = new ArrayList<>();
        weaponList.add("Lock_Rifle");
        weaponList.add("Basic Mode");
        System.out.println(weaponList);
        clientCommands.put(SHOOT, weaponList);
        netMessage = new NetMessage(clientCommands);
        InternalMessage internalMessage = commandHandler.convertNetMessage(netMessage, game);
        assertEquals(weapon, internalMessage.getCommands().get(SHOOT).get(0));
        assertEquals(PlayerAction.SHOOT, internalMessage.getCommands().get(KEY_ORDER).get(0));
    }

    @Test
    public void testAmmoColor() {
        NetMessage netMessage;
        Map<String, List<String>> clientCommands = new HashMap<>();
        List<String> action = new ArrayList<>();
        action.add(POWERUP);
        clientCommands.put(KEY_ORDER, action);
        PowerUp powerUp = getPowerUp("Red_TargetingScope");
        p1.getCharacterState().getPowerUpBag().clear();
        p1.getCharacterState().getPowerUpBag().add(powerUp);
        List<String> powerUpList = new ArrayList<>();
        powerUpList.add("Red_TargetingScope");
        powerUpList.add("P2");
        powerUpList.add("RED");
        clientCommands.put(POWERUP, powerUpList);
        netMessage = new NetMessage(clientCommands);
        InternalMessage internalMessage = commandHandler.convertNetMessage(netMessage, game);
        assertEquals(powerUp, internalMessage.getCommands().get(POWERUP).get(0));
        assertEquals(PlayerAction.POWERUP, internalMessage.getCommands().get(KEY_ORDER).get(0));

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