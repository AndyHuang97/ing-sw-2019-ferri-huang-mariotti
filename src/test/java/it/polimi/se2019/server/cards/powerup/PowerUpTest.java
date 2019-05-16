package it.polimi.se2019.server.cards.powerup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.polimi.se2019.server.deserialize.*;
import it.polimi.se2019.server.games.Deck;
import it.polimi.se2019.server.games.player.Player;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import static org.junit.Assert.*;

public class PowerUpTest {

    DynamicDeserializerFactory factory;
    Deck<PowerUp> powerUpDeck;
    Player p1, p2;

    @Before
    public void setUp()  {
        factory = new DynamicDeserializerFactory();

        factory.registerDeserializer("powerupdeck", new PowerUpDeserializerSupplier());
        factory.registerDeserializer("actions", new ActionsDeserializerSupplier());
        factory.registerDeserializer("actionunit", new ActionUnitDeserializerSupplier());
        factory.registerDeserializer("effects", new EffectDeserializerSupplier());
        factory.registerDeserializer("conditions", new ConditionDeserializerSupplier());

        PowerUpDeserializer powerUpDeserializer = (PowerUpDeserializer) factory.getDeserializer("powerupdeck");
        BufferedReader bufferedReader;
        String powerUpPath = "src/main/resources/json/powerups/powerups.json";
        powerUpDeck = null;

        try {
            bufferedReader = new BufferedReader(new FileReader(powerUpPath));

            JsonParser parser = new JsonParser();
            JsonObject json = parser.parse(bufferedReader).getAsJsonObject();
            powerUpDeck = powerUpDeserializer.deserialize(json, factory);

            try {
                bufferedReader.close();
            }catch (IOException e) {
                Assert.fail("Buffered reader could not close correctly.");
            }
        } catch (FileNotFoundException e) {
            Assert.fail("File not found.");
        } catch (ClassNotFoundException f) {
            Assert.fail("Class not found.");
        }
    }

    @After
    public void tearDown() {
        factory = null;
        powerUpDeck = null;
        p1 = null;
        p2 = null;
    }

    @Test
    public void testTargetingScope() {
        PowerUp powerUp = powerUpDeck.drawCard();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        //System.out.println(gson.toJson(powerUp));

    }
}