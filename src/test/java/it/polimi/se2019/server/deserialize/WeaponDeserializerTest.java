package it.polimi.se2019.server.deserialize;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.polimi.se2019.server.actions.ActionUnit;
import it.polimi.se2019.server.actions.conditions.Condition;
import it.polimi.se2019.server.actions.conditions.Distance;
import it.polimi.se2019.server.actions.conditions.IsTargetVisible;
import it.polimi.se2019.server.actions.effects.DamageTarget;
import it.polimi.se2019.server.actions.effects.Effect;
import it.polimi.se2019.server.actions.effects.MarkTarget;
import it.polimi.se2019.server.cards.weapons.Weapon;
import it.polimi.se2019.server.games.player.AmmoColor;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

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
        try {
            bufferedReader = new BufferedReader(new FileReader(path));
            JsonParser parser = new JsonParser();
            JsonObject json = parser.parse(bufferedReader).getAsJsonObject();

            final Weapon weapon = weaponDeserializer.deserialize(json, factory);
            try {
                bufferedReader.close();
            }catch (IOException e) {
                Assert.fail("Buffered reader could not close correctly.");
            }

            /*
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonWeapon = gson.toJson(weapon);
            System.out.println(jsonWeapon);
             */
            final Weapon expectedWeapon = new Weapon(null, "Whisper",
                    null,null, null);
            expectedWeapon.setPickUpCost(Arrays.asList(AmmoColor.BLUE, AmmoColor.YELLOW));
            expectedWeapon.setReloadCost(Arrays.asList(AmmoColor.BLUE, AmmoColor.BLUE, AmmoColor.YELLOW));
            expectedWeapon.setOptionalEffectList(Arrays.asList());
            List<Effect> effectList = Arrays.asList(new DamageTarget( 2),
                    new MarkTarget(1));
            List<Condition> conditionList = Arrays.asList(new IsTargetVisible(),
                    new Distance(2));
            expectedWeapon.setActionUnitList(Arrays.asList(
                    new ActionUnit(true, "Basic Mode", effectList,conditionList)));
            /*
            jsonWeapon = gson.toJson(expectedWeapon);
            System.out.println(jsonWeapon);
             */

            Assert.assertEquals(expectedWeapon.getName(), weapon.getName());
            Assert.assertEquals(true , weapon.getOptionalEffectList().containsAll(expectedWeapon.getOptionalEffectList()));
            Assert.assertEquals(true, weapon.getPickUpCost().containsAll(expectedWeapon.getPickUpCost()));
            Assert.assertEquals(true, weapon.getReloadCost().containsAll(expectedWeapon.getReloadCost()));
            Assert.assertEquals(expectedWeapon.getActionUnitList().size(), weapon.getActionUnitList().size());
            IntStream.range(0, weapon.getActionUnitList().size())
                    .forEach(i -> IntStream.range(0, weapon.getActionUnitList().get(0).getConditionList().size())
                            .forEach(j ->
                                    Assert.assertEquals(expectedWeapon.getActionUnitList().get(i).getConditionList().get(j).getClass(),
                                            weapon.getActionUnitList().get(i).getConditionList().get(j).getClass())
                            )
                    );
            IntStream.range(0, weapon.getActionUnitList().size())
                    .forEach(i -> IntStream.range(0, weapon.getActionUnitList().get(0).getEffectList().size())
                            .forEach(j ->
                                    Assert.assertEquals(expectedWeapon.getActionUnitList().get(i).getEffectList().get(j).getClass(),
                                            weapon.getActionUnitList().get(i).getEffectList().get(j).getClass())
                            )
                    );
        } catch (FileNotFoundException e) {
            Assert.fail("File not found.");
        } catch (ClassNotFoundException e) {
            Assert.fail("Class not found.");
        }


    }
}