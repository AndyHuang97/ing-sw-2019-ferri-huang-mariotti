package it.polimi.se2019.server.deserialize;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.polimi.se2019.server.cards.ammocrate.AmmoCrate;
import it.polimi.se2019.server.cards.powerup.PowerUp;
import it.polimi.se2019.server.cards.weapons.Weapon;
import it.polimi.se2019.server.games.Deck;
import it.polimi.se2019.server.games.board.Board;
import it.polimi.se2019.util.DeserializerConstants;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CardDeserializerTest {

    DynamicDeserializerFactory factory;

    @Before
    public void setUp()  {
        factory = new DynamicDeserializerFactory();

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

    @After
    public void tearDown() {
        factory = null;
    }

    @Test
    public void testDeserialize() {
        AmmoCrateDeserializer ammoCrateDeserializer = (AmmoCrateDeserializer) factory.getDeserializer(DeserializerConstants.AMMOCRATEDECK);
        PowerUpDeserializer powerUpDeserializer = (PowerUpDeserializer) factory.getDeserializer(DeserializerConstants.POWERUPDECK);
        WeaponDeckDeserializer weaponDeckDeserializer = (WeaponDeckDeserializer) factory.getDeserializer(DeserializerConstants.WEAPONDECK);

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
        Deck<Weapon> weaponDeck = DirectDeserializers.deserialzeWeaponDeck();
        Deck<PowerUp> powerUpDeck = DirectDeserializers.deserialzePowerUpDeck();
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
        //powerUpDeck.shuffle();
        PowerUp pU;
        i = 0;
        while((pU = powerUpDeck.drawCard()) != null) {
            System.out.println(pU.getName());
            System.out.println("1): " + pU.getName().split("_")[1]);
            //System.out.println(pU.getPowerUpColor());
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