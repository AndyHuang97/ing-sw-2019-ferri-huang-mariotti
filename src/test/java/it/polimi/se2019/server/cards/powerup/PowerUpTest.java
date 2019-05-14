package it.polimi.se2019.server.cards.powerup;

import it.polimi.se2019.server.deserialize.*;
import it.polimi.se2019.server.games.player.Player;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PowerUpTest {

    DynamicDeserializerFactory factory;
    Player p1, p2;

    @Before
    public void setUp()  {
        factory = new DynamicDeserializerFactory();

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
    public void testTargetingScopeBehavior() {

    }
}