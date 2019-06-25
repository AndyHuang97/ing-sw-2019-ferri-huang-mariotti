package it.polimi.se2019.server.deserialize;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.polimi.se2019.server.cards.ammocrate.AmmoCrate;
import it.polimi.se2019.server.cards.powerup.PowerUp;
import it.polimi.se2019.server.cards.weapons.Weapon;
import it.polimi.se2019.server.games.Deck;
import it.polimi.se2019.server.games.board.Board;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Logger;

public class DirectDeserializers {

    private static String MAP = "maps/map";
    private static String JSON_PATH = "src/main/resources/json/";
    private static String WEAPON = "weapons/weapons.json";
    private static String POWERUP = "powerups/powerups.json";
    private static String AMMOCRATE = "ammocrates/ammocrates.json";
    private static String JSON = ".json";

    static DynamicDeserializerFactory factory = new DynamicDeserializerFactory();


    public DirectDeserializers() {
        factory.registerDeserializer("tile", new TileDeserializerSupplier());
        factory.registerDeserializer("ammmocratedeck", new AmmoCrateDeserializerSupplier());
        factory.registerDeserializer("powerupdeck", new PowerUpDeserializerSupplier());
        factory.registerDeserializer("weapondeck", new WeaponDeckDeserializerSuppier());
        factory.registerDeserializer("weapon", new WeaponDeserializerSupplier());
        factory.registerDeserializer("actions", new ActionsDeserializerSupplier());
        factory.registerDeserializer("optionaleffects", new OptionalEffectDeserializerSupplier());
        factory.registerDeserializer("actionunit", new ActionUnitDeserializerSupplier());
        factory.registerDeserializer("effects", new EffectDeserializerSupplier());
        factory.registerDeserializer("conditions", new ConditionDeserializerSupplier());
    }

    public static Board deserializeBoard(String mapIndex) {

        BoardDeserializer boardDeserializer = new BoardDeserializer();
        String path = JSON_PATH+MAP+mapIndex+JSON;
        Board board = null;

        board = (Board) deserialize(board, boardDeserializer, path);

        return board;
    }

    public static Deck<Weapon> deserialzerWeaponDeck() {
        WeaponDeckDeserializer weaponDeckDeserializer = (WeaponDeckDeserializer) factory.getDeserializer("weapondeck");

        String path = JSON_PATH+WEAPON;

        Deck<Weapon> weaponDeck = null;

        weaponDeck = (Deck<Weapon>) deserialize(weaponDeck, weaponDeckDeserializer, path);

        return weaponDeck;
    }

    public static Deck<PowerUp> deserialzerPowerUpDeck() {
        PowerUpDeserializer powerUpDeserializer = (PowerUpDeserializer) factory.getDeserializer("powerupdeck");

        String path = JSON_PATH+POWERUP;

        Deck<PowerUp> powerUpDeck = null;

        powerUpDeck = (Deck<PowerUp>) deserialize(powerUpDeck, powerUpDeserializer, path);

        return powerUpDeck;
    }

    public static Deck<AmmoCrate> deserializeAmmoCrate() {
        AmmoCrateDeserializer ammoCrateDeserializer = (AmmoCrateDeserializer) factory.getDeserializer("ammmocratedeck");

        String path = JSON_PATH+AMMOCRATE;

        Deck<AmmoCrate> ammoCrateDeck = null;

        ammoCrateDeck = (Deck<AmmoCrate>) deserialize(ammoCrateDeck, ammoCrateDeserializer, path);

        return ammoCrateDeck;
    }

    private static Object deserialize(Object object, RandomDeserializer deserializer, String path) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(path))) {

            JsonParser parser = new JsonParser();
            JsonObject json = parser.parse(bufferedReader).getAsJsonObject();
            object = deserializer.deserialize(json, factory);
        } catch (IOException | ClassNotFoundException e) {
            Logger.getGlobal().warning(e.toString());
        }
        return object;
    }
}
