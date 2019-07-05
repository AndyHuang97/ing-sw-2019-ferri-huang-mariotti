package it.polimi.se2019.server.deserialize;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.polimi.se2019.server.cards.ammocrate.AmmoCrate;
import it.polimi.se2019.server.cards.powerup.PowerUp;
import it.polimi.se2019.server.cards.weapons.Weapon;
import it.polimi.se2019.server.games.Deck;
import it.polimi.se2019.server.games.board.Board;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.CharacterState;
import it.polimi.se2019.util.DeserializerConstants;

import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class DirectDeserializers {

    private static final String MAP = "maps/map";
    private static final String JSON_PATH = "json/";
    private static final String WEAPON = "weapons/weapons.json";
    private static final String POWERUP = "powerups/powerups.json";
    private static final String AMMOCRATE = "ammocrates/ammocrates.json";
    private static final String JSON = ".json";

    static DynamicDeserializerFactory factory = new DynamicDeserializerFactory();


    public DirectDeserializers() {
        factory.registerDeserializer(DeserializerConstants.TILE, new TileDeserializerSupplier());
        factory.registerDeserializer(DeserializerConstants.AMMOCRATEDECK, new AmmoCrateDeserializerSupplier());
        factory.registerDeserializer(DeserializerConstants.POWERUPDECK, new PowerUpDeserializerSupplier());
        factory.registerDeserializer(DeserializerConstants.WEAPONDECK, new WeaponDeckDeserializerSuppier());
        factory.registerDeserializer(DeserializerConstants.WEAPON, new WeaponDeserializerSupplier());
        factory.registerDeserializer(DeserializerConstants.ACTIONS, new ActionsDeserializerSupplier());
        factory.registerDeserializer(DeserializerConstants.OPTIONALEFFECTS, new OptionalEffectDeserializerSupplier());
        factory.registerDeserializer(DeserializerConstants.ACTIONUNIT, new ActionUnitDeserializerSupplier());
        factory.registerDeserializer(DeserializerConstants.EFFECTS, new EffectDeserializerSupplier());
        factory.registerDeserializer(DeserializerConstants.CONDITIONS, new ConditionDeserializerSupplier());
    }

    public static Board deserializeBoard(String mapIndex) {

        BoardDeserializer boardDeserializer = new BoardDeserializer();
        String path = JSON_PATH+MAP+mapIndex+JSON;
        Board board = null;

        board = (Board) deserialize(board, boardDeserializer, path);

        return board;
    }

    public static Deck<Weapon> deserialzeWeaponDeck() {
        WeaponDeckDeserializer weaponDeckDeserializer = (WeaponDeckDeserializer) factory.getDeserializer(DeserializerConstants.WEAPONDECK);

        String path = JSON_PATH+WEAPON;

        Deck<Weapon> weaponDeck = null;

        weaponDeck = (Deck<Weapon>) deserialize(weaponDeck, weaponDeckDeserializer, path);

        return weaponDeck;
    }

    public static Deck<PowerUp> deserialzePowerUpDeck() {
        PowerUpDeserializer powerUpDeserializer = (PowerUpDeserializer) factory.getDeserializer(DeserializerConstants.POWERUPDECK);

        String path = JSON_PATH+POWERUP;

        Deck<PowerUp> powerUpDeck = null;

        powerUpDeck = (Deck<PowerUp>) deserialize(powerUpDeck, powerUpDeserializer, path);

        return powerUpDeck;
    }

    public static Deck<AmmoCrate> deserializeAmmoCrate() {
        AmmoCrateDeserializer ammoCrateDeserializer = (AmmoCrateDeserializer) factory.getDeserializer(DeserializerConstants.AMMOCRATEDECK);

        String path = JSON_PATH+AMMOCRATE;

        Deck<AmmoCrate> ammoCrateDeck = null;

        ammoCrateDeck = (Deck<AmmoCrate>) deserialize(ammoCrateDeck, ammoCrateDeserializer, path);

        return ammoCrateDeck;
    }

    public static Deck<PowerUp> deserializePowerUpDeck(Deck<PowerUp> deck) {
        List<PowerUp> powerUpList = new ArrayList<>();

        if (deck != null) {
            PowerUp powerUp = deck.drawCard();
            while (powerUp != null) {
                powerUp = getPowerUp(powerUp.getName());
                powerUpList.add(powerUp);
                powerUp = deck.drawCard();
            }
        }
        return new Deck<>(powerUpList);
    }

    public static Deck<Weapon> deserializeWeaponDeck(Deck<Weapon> deck) {
        List<Weapon> weaponList = new ArrayList<>();

        Weapon weapon = deck.drawCard();
        while (weapon != null) {
            weapon = getWeapon(weapon.getName());
            weaponList.add(weapon);
            weapon = deck.drawCard();
        }
        return new Deck<>(weaponList);
    }

    public static Deck<AmmoCrate> deserializeAmmoCrateDeck(Deck<AmmoCrate> deck) {
        List<AmmoCrate> ammoCrateList = new ArrayList<>();

        AmmoCrate ammoCrate = deck.drawCard();
        while (ammoCrate != null) {
            ammoCrate = getAmmoCrate(ammoCrate.getName());
            ammoCrateList.add(ammoCrate);
            ammoCrate = deck.drawCard();
        }
        return new Deck<>(ammoCrateList);
    }

    public static Board deserializeBoardCrates(Board board) {
        board.getTileList().stream().filter(Objects::nonNull).filter(Tile::isSpawnTile).forEach(tile -> tile.setWeaponCrate(tile.getWeaponCrate().stream().map(weapon -> getWeapon(weapon.getName())).collect(Collectors.toList())));
        board.getTileList().stream().filter(Objects::nonNull).filter(tile -> !tile.isSpawnTile()).forEach(tile -> tile.setAmmoCrate(getAmmoCrate(tile.getAmmoCrate().getName())));
        return board;
    }

    public static CharacterState deserializeCharacterState(CharacterState characterState, Board board) {
        characterState.setTile(board.getTileFromID(characterState.getTile().getId()));
        characterState.setPowerUpBag(characterState.getPowerUpBag().stream().map(powerUp -> getPowerUp(powerUp.getName())).collect(Collectors.toList()));
        characterState.setWeaponBag(characterState.getWeaponBag().stream().map(weapon -> getWeapon(weapon.getName())).collect(Collectors.toList()));
        return characterState;
    }

    public static List<PowerUp> deserializePowerUpList(List<PowerUp> powerUpList) {
        return powerUpList.stream().map(powerUp -> getPowerUp(powerUp.getName())).collect(Collectors.toList());
    }


    private static Object deserialize(Object object, RandomDeserializer deserializer, String path) {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(DirectDeserializers.class.getClassLoader().getResource(path).openStream()))) {

            JsonParser parser = new JsonParser();
            JsonObject json = parser.parse(bufferedReader).getAsJsonObject();
            object = deserializer.deserialize(json, factory);
        } catch (IOException | ClassNotFoundException | NullPointerException e) {
            Logger.getGlobal().warning(e.toString());
        }
        return object;
    }

    private static PowerUp getPowerUp(String cardName) {
        Deck<PowerUp> deck = DirectDeserializers.deserialzePowerUpDeck();
        PowerUp card = deck.drawCard();

        while(!card.getName().equals(cardName)) {
            card = deck.drawCard();
        }
        //System.out.println(card.getName());
        return card;
    }

    private static Weapon getWeapon(String cardName) {
        Deck<Weapon> deck = DirectDeserializers.deserialzeWeaponDeck();
        Weapon card = deck.drawCard();

        while(!card.getName().equals(cardName)) {
            card = deck.drawCard();
        }

        return card;
    }

    private static AmmoCrate getAmmoCrate(String cardName) {
        Deck<AmmoCrate> deck = DirectDeserializers.deserializeAmmoCrate();
        AmmoCrate card = deck.drawCard();

        while(!card.getName().equals(cardName)) {
            card = deck.drawCard();
        }

        return card;
    }
}

