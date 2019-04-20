package it.polimi.SE2019.server.deserialize;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.polimi.SE2019.server.cards.weapons.Weapon;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.UnknownHostException;

import static org.junit.Assert.*;

public class WeaponDeserializerTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testDeserialize() {
        DynamicDeserializerFactory factory = new DynamicDeserializerFactory();

        factory.registerDeserializer("weapon", new WeaponDeserializerSupplier());
        factory.registerDeserializer("actions", new ActionsDeserializerSupplier());
        factory.registerDeserializer("actionunit", new ActionUnitDeserializerSupplier());
        factory.registerDeserializer("effects", new EffectDeserializerSupplier());
        factory.registerDeserializer("conditions", new ConditionDeserializerSupplier());

        WeaponDeserializer weaponDeserializer = (WeaponDeserializer) factory.getDeserializer("weapon");

        String path = "src/main/java/it/polimi/SE2019/server/deserialize/data//cards.json";
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
                System.out.println("Buffered reader could not close correctly.");
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
        } catch (ClassNotFoundException f) {
            System.out.println("Class not found.");
        }



        Gson gson = new Gson();
        String json = gson.toJson(weapon);
        System.out.println(json);

        //assertArrayEquals();
    }
}