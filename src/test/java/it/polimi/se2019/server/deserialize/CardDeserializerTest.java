package it.polimi.se2019.server.deserialize;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.polimi.se2019.server.cards.ammocrate.AmmoCrate;
import it.polimi.se2019.server.cards.powerup.PowerUp;
import it.polimi.se2019.server.cards.weapons.Weapon;
import it.polimi.se2019.server.games.Deck;
import it.polimi.se2019.server.games.board.Board;
import org.junit.After;
import org.junit.Assert;
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
        factory.registerDeserializer("weapondeck", new WeaponDeckDeserializerSuppier());
        factory.registerDeserializer("weapon", new WeaponDeserializerSupplier());
        factory.registerDeserializer("actions", new ActionsDeserializerSupplier());
        factory.registerDeserializer("optionaleffects", new OptionalEffectDeserializerSupplier());
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
        WeaponDeckDeserializer weaponDeckDeserializer = (WeaponDeckDeserializer) factory.getDeserializer("weapondeck");

        String ammoCratePath = "src/main/resources/json/ammocrates/ammocrates.json";
        String powerUpPath = "src/main/resources/json/powerups/powerups.json";
        String weaponPath = "src/main/resources/json/weapons/weapons.json";

        BufferedReader acbufferedReader, pubufferedReader, wbufferedReader;
        Deck<AmmoCrate> ammoCrateDeck = null;
        Deck<PowerUp> powerUpDeck = null;
        Deck<Weapon> weaponDeck = null;

        try {
            acbufferedReader = new BufferedReader(new FileReader(ammoCratePath));
            pubufferedReader = new BufferedReader(new FileReader(powerUpPath));
            wbufferedReader = new BufferedReader(new FileReader(weaponPath));

            JsonParser parser = new JsonParser();
            JsonObject acjson = parser.parse(acbufferedReader).getAsJsonObject();
            JsonObject pujson = parser.parse(pubufferedReader).getAsJsonObject();
            JsonObject wjson = parser.parse(wbufferedReader).getAsJsonObject();

            ammoCrateDeck = ammoCrateDeserializer.deserialize(acjson, factory);
            powerUpDeck = powerUpDeserializer.deserialize(pujson, factory);
            weaponDeck = weaponDeckDeserializer.deserialize(wjson, factory);


            try {
                acbufferedReader.close();
                pubufferedReader.close();
                wbufferedReader.close();
            }catch (IOException e) {
                Assert.fail("Buffered reader could not close correctly.");
            }
        } catch (FileNotFoundException e) {
            Assert.fail("File not found.");
        } catch (ClassNotFoundException f) {
            Assert.fail("Class not found.");
        }
    }

    @Test
    public void testDirectDeserializers() {
        new DirectDeserializers();
        Board board = DirectDeserializers.deserializeBoard("0");
        Deck<Weapon> weaponDeck = DirectDeserializers.deserialzerWeaponDeck();
        Deck<PowerUp> powerUpDeck = DirectDeserializers.deserialzerPowerUpDeck();
        Deck<AmmoCrate> ammoCrateDeck = DirectDeserializers.deserializeAmmoCrate();

        assertNotNull(board);
        assertNotNull(weaponDeck);
        assertNotNull(powerUpDeck);
        assertNotNull(ammoCrateDeck);

        assertEquals("0", board.getId());
        System.out.println("BoardId: "+board.getId());
        System.out.println("Board:" + board);
        System.out.println("Weapon:"+weaponDeck);
        Weapon w;
        int i = 0;
        while((w = weaponDeck.drawCard()) != null) {
            System.out.println(w.getName());
             i++;
        }
        assertEquals(21, i);
        System.out.println();
        System.out.println("PowerUp:"+powerUpDeck);
        powerUpDeck.shuffle();
        PowerUp pU;
        i = 0;
        while((pU = powerUpDeck.drawCard()) != null) {
            System.out.println(pU.getName());
            i++;
        }
        assertEquals(24, i);
        System.out.println();
        System.out.println("AmmoCrate:"+ammoCrateDeck);
        ammoCrateDeck.shuffle();
        AmmoCrate aC;
        i = 0;
        while((aC = ammoCrateDeck.drawCard()) != null) {
            System.out.println(aC.getName());
            i++;
        }
        assertEquals(36,i);



    }
}