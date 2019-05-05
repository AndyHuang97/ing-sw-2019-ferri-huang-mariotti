package it.polimi.se2019.server.deserialize;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.polimi.se2019.server.cards.weapons.Weapon;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class WeaponDeserializerTest {
    DynamicDeserializerFactory factory = new DynamicDeserializerFactory();
    WeaponDeserializer weaponDeserializer = new WeaponDeserializer();

    @Before
    public void setUp() throws Exception {
        factory.registerDeserializer("actions", new ActionsDeserializerSupplier());
        factory.registerDeserializer("optionaleffects", new OptionalEffectDeserializerSupplier());
        factory.registerDeserializer("actionunit", new ActionUnitDeserializerSupplier());
        factory.registerDeserializer("effects", new EffectDeserializerSupplier());
        factory.registerDeserializer("conditions", new ConditionDeserializerSupplier());
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testDeserialize() {
        String path = "src/test/java/it/polimi/se2019/server/deserialize/data/cards.json";
        BufferedReader bufferedReader;
        Weapon weapon = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(path));
            JsonParser parser = new JsonParser();
            JsonObject json = parser.parse(bufferedReader).getAsJsonObject();

            weapon = weaponDeserializer.deserialize(json, factory);
            try {
                bufferedReader.close();
            }catch (IOException e) {
                Assert.fail("Buffered reader could not close correctly.");
            }
        } catch (FileNotFoundException e) {
            Assert.fail("File not found.");
        } catch (ClassNotFoundException e) {
            Assert.fail("Class not found.");
        }
        /*
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(weapon);
        System.out.println(json);
         */

        Assert.assertEquals(weapon.getName(), "Whisper");
    }
}