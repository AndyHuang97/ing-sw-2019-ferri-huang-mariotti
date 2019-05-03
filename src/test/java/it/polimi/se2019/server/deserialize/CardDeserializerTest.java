package it.polimi.se2019.server.deserialize;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.polimi.se2019.server.cards.ammocrate.AmmoCrate;
import it.polimi.se2019.server.cards.powerup.PowerUp;
import it.polimi.se2019.server.cards.weapons.Weapon;
import it.polimi.se2019.server.games.Deck;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class CardDeserializerTest {

    DynamicDeserializerFactory factory;

    @Before
    public void setUp()  {
        factory = new DynamicDeserializerFactory();

        factory.registerDeserializer("ammmocratedeck", new AmmoCrateDeserializerSupplier());
        factory.registerDeserializer("powerupdeck", new PowerUpDeserializerSupplier());
        factory.registerDeserializer("actions", new ActionsDeserializerSupplier());
        factory.registerDeserializer("actionunit", new ActionUnitDeserializerSupplier());
        factory.registerDeserializer("effects", new EffectDeserializerSupplier());
        factory.registerDeserializer("conditions", new ConditionDeserializerSupplier());
    }

    @After
    public void tearDown() {
        factory = null;
    }

    @Test
    public void testDeserialize() {
        AmmoCrateDeserializer ammoCrateDeserializer = (AmmoCrateDeserializer) factory.getDeserializer("ammmocratedeck");
        PowerUpDeserializer powerUpDeserializer = (PowerUpDeserializer) factory.getDeserializer("powerupdeck");

        String ammoCratePath = "src/main/java/it/polimi/se2019/resources/json/ammocrates/ammocrates.json";
        String powerUpPath = "src/main/java/it/polimi/se2019/resources/json/powerups/powerups.json";

        BufferedReader acbufferedReader, pubufferedReader;
        Deck<AmmoCrate> ammoCrateDeck = null;
        Deck<PowerUp> powerUpDeck = null;

        try {
            acbufferedReader = new BufferedReader(new FileReader(ammoCratePath));
            pubufferedReader = new BufferedReader(new FileReader(powerUpPath));
            JsonParser parser = new JsonParser();
            JsonObject acjson = parser.parse(acbufferedReader).getAsJsonObject();
            JsonObject pujson = parser.parse(pubufferedReader).getAsJsonObject();

            ammoCrateDeck = ammoCrateDeserializer.deserialize(acjson, factory);
            powerUpDeck = powerUpDeserializer.deserialize(pujson,factory);

            try {
                acbufferedReader.close();
            }catch (IOException e) {
                System.out.println("Buffered reader could not close correctly.");
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
        } catch (ClassNotFoundException f) {
            System.out.println("Class not found.");
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(ammoCrateDeck);
        //System.out.println(json);
        json = gson.toJson(powerUpDeck);
        System.out.println(json);


    }
}